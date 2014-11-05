package net.datenstrudel.bulbs.core.application.messaging.notification.handling;

import com.rabbitmq.client.*;
import net.datenstrudel.bulbs.core.domain.model.messaging.ProcessNotRepeatableException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author Thomas Wendzinski
 */
public abstract class RabbitMqExchangeListener {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(RabbitMqExchangeListener.class);
    @Autowired
    @Qualifier(value = "rabbitConnectionFactory")
    private ConnectionFactory rabbitConnectionFactory;
    
    private Channel channel;
    private String queueName;
    private String consumerTag;
    private volatile boolean cancelOk = false;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public RabbitMqExchangeListener(){
        
    }
    @PostConstruct
    public void init() throws Exception{
        this.attachToQueue(false);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    protected abstract String topicName();
    protected abstract String[] listensToEvents();
    protected abstract void filteredDispatch(String type, String message);
    protected String queueName(){
        return this.getClass().getName();
    }
    
    public void reconnect4Recover()throws IOException{
        Long start = System.currentTimeMillis();
        if(this.channel != null && this.channel.isOpen() ){
            try{
                this.channel.basicCancel(consumerTag);
            }catch (IOException ex){
                log.error(ex.getMessage());
                setCancelOk(true);
            }
            do{
                if( (System.currentTimeMillis() - start) > 10000 ){
                    log.error("Couldn' cancel RMQ Channel for reconnection!");
                    throw new IllegalStateException("Couldn't cancel RMQ Channel for reconnection!");
                }
            }while(!cancelOk);
            setCancelOk(false); // reset
            this.channel.close();
            this.channel.abort();
            
        }
        attachToQueue(true);
        if(log.isDebugEnabled()) 
            log.debug("Reconnected in " +(System.currentTimeMillis() - start)+" ms");
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void attachToQueue(boolean recover)throws IOException{
        Connection cn = rabbitConnectionFactory.createConnection();
        this.channel = cn.createChannel(false);
        channel.exchangeDeclare( this.topicName(), "topic", true );
        this.queueName = channel.queueDeclare(
                this.topicName() + ".__." + this.queueName() 
                , true, false, false, null).getQueue();
        if(recover){
            channel.basicRecover(true);
        }
        if(!recover) RabbitMqListenerRegistry.registerListener(this, 10000);
        channel.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException cause) {
                if( !cause.isInitiatedByApplication() ){
                    log.error("Channel shutdown: " + cause.getMessage(), cause);
                    log.info("Re creating channel..");
                    try{
                        attachToQueue(true);
                    }catch(IOException ex){
                        log.error("Couldn't create new channel due to " + ex, ex);
                    }
                }else{
                    if(log.isDebugEnabled())
                        log.debug("Channel shutdown: " + cause.getMessage());
                }
            }
        });
        this.consumerTag = this.topicName() + ".__." + this.queueName() + UUID.randomUUID().toString();
        registerConsumer(channel, queueName, consumerTag);
    }
    
    private void registerConsumer(
            final Channel channelLocal, 
            String queueNameLocal, 
            String consumerTag)throws IOException{
        
        for (String routingKey : listensToEvents()) {
            channelLocal.queueBind(queueNameLocal, topicName(), routingKey);
        }
        channelLocal.basicConsume(
                queueNameLocal, 
                false, 
                consumerTag,
                new DefaultConsumer(channel){
            
            @Override
            public void handleCancelOk(String consumerTag) {
                super.handleCancelOk(consumerTag);
                setCancelOk(true);
            }
            @Override
            public void handleRecoverOk(String consumerTag) {
                super.handleRecoverOk(consumerTag); 
                if(log.isDebugEnabled()){
                    log.info("Recovering OK Triggered for consumer tag: " + consumerTag);
                }
            }
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, 
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                if(log.isDebugEnabled()){
                    log.debug("|--[ <- ] Retrieved RMQ Message for type["+properties.getContentType()+"]");
                    log.debug(" --[ <- ] .. with routing key["+envelope.getRoutingKey()+"]");
                }
                 try{
                    filteredDispatch(properties.getContentType(), new String(body));
                    channelLocal.basicAck(envelope.getDeliveryTag(), false);
                }catch(ProcessNotRepeatableException pnrex){
                    log.warn("Message processing aborted due to " + pnrex.getMessage());
                    log.error("NAcking Tag: " + envelope.getDeliveryTag());
                    channelLocal.basicNack(envelope.getDeliveryTag(), false, false);
                }catch(Exception ex){
                    log.error("Error on notification processing", ex);
                    if(!envelope.isRedeliver()){
                        log.error("NNAcking Tag: " + envelope.getDeliveryTag());
                        channelLocal.basicNack(envelope.getDeliveryTag(), false, true);
                    }else{
                        log.error(" [XXX] Retry of requeued notification failed. Leaving unacked in order it can be recovered later.");
                        log.error(" [XXX] ", ex);
                    }
                }finally{
                     
                }
            }
                }
        );
    }
    
    private void setCancelOk(boolean cancelOk){
        this.cancelOk = cancelOk;
    }
}
