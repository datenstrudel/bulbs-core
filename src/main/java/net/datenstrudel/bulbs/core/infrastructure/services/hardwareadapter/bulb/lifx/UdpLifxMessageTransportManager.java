package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Thomas Wendzinski.
 */
@Component
public class UdpLifxMessageTransportManager {

    public static final Logger log = LoggerFactory.getLogger(UdpLifxMessageTransportManager.class);
    public static final int LIFX_STD_PORT = 56700;
    public static final int SOCKET_TIMEOUT = 600;

    private final List<InetAddress> LOCAL_ADDRESSES;
    private final TaskExecutor listenerExecutor;
    private DatagramSocket udpSocket;

    private volatile ConcurrentHashMap<LifxPacketType, CompletableFuture<LifxMessage[]>> clientsWaiting
            = new ConcurrentHashMap<>(3);
    private volatile boolean listening = false;

    @Autowired
    public UdpLifxMessageTransportManager(@Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor) {
        this.listenerExecutor = taskExecutor;
        try {
            this.LOCAL_ADDRESSES = allLocalAddresses();
        } catch (SocketException e) {
            throw new IllegalStateException("Cannot construct due to local addresses not resolvable", e);
        }
    }
    @PostConstruct
    public void init() {
        StringJoiner joiner = new StringJoiner(" ; ");
        this.LOCAL_ADDRESSES.stream().forEach(a -> joiner.add(a.getHostAddress()));
        log.info("Local adresses found: {}", joiner.toString());
    }

    //~ ////////////////////////////////////////////////////////////////////////////
    public CompletableFuture<LifxMessage[]> sendAndReceiveAggregated(
            LifxMessage<?> messageOut, LifxPacketType expectedResponseType) throws BulbBridgeHwException {

        CompletableFuture<LifxMessage[]> res = new CompletableFuture<>();
        provideNewUdpSocket(LIFX_STD_PORT);
        sendDatagramMessage(messageOut);
        listenForAnswersOf(messageOut, expectedResponseType, res);
        return res;
    }

    public InetAddress lanMulticastAddress() throws BulbBridgeHwException {
        try {
            return InetAddress.getByName("192.168.1.255");
        } catch (UnknownHostException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }
    public InetAddress localhostAddress() throws BulbBridgeHwException {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            log.debug("Defined localhost as: {}", localHost.getHostAddress() );
            return localHost;
        } catch (UnknownHostException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }

    //~ /////////////////////////////////////////////////////////////////////////////
    private void listenForAnswersOf(
            LifxMessage messageSent,
            LifxPacketType expectedResponseType,
            CompletableFuture<LifxMessage[]> results) {

        this.clientsWaiting.put(expectedResponseType, results);

        if (this.listening) return;
        this.listening = true;
        log.debug("|-- Going to start listen task..");
        if(this.listenerExecutor instanceof AsyncTaskExecutor){
            ((AsyncTaskExecutor)this.listenerExecutor).execute(
                    new UdpListenExcecutor(clientsWaiting), AsyncTaskExecutor.TIMEOUT_IMMEDIATE );
        }else{
            this.listenerExecutor.execute(new UdpListenExcecutor(clientsWaiting));
        }
    }

    private void sendDatagramMessage(LifxMessage message ) throws BulbBridgeHwException {
        if(log.isDebugEnabled()){
            log.debug("|-- Going to send data on udpSocket address [{}} and port [{}]..", message.getAddress(), message.getPort());
            log.debug(" -- " + message);
        }

        DatagramPacket packet = new DatagramPacket(
                message.toBytes(), message.toBytes().length, message.getAddress(), message.getPort()
        );
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }
    /**
     * Retrieve a UDP message and discard messages from senders known to be unlikely
     * @return
     * @throws BulbBridgeHwException
     */
    private Optional<LifxMessage> retrieveDatagramMessage() throws BulbBridgeHwException {
        byte[] respPayload = new byte[200];
        DatagramPacket destPacket = new DatagramPacket(respPayload, respPayload.length);
        try {
            this.udpSocket.receive(destPacket);
            LifxMessage res = LifxMessage.fromBytes(
                    destPacket.getData(), destPacket.getAddress(), destPacket.getPort());

            if( ignorePacket(destPacket) ){
                log.debug("Message has been discarded from address: {}", destPacket.getAddress());
                return Optional.empty();
            }
            log.debug(":) -- Retrieved packet of {} Bytes from address [{}]", destPacket.getLength(), destPacket.getAddress());
//            log.debug(":) -- {}", res);
            return Optional.of(res);
        }catch(SocketTimeoutException e){
//            log.info(":( .. receive timeout..");
            return Optional.empty();
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }

    }
    private List<InetAddress> allLocalAddresses() throws SocketException {
        List<InetAddress> addrList = new LinkedList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface netIe;
        while(networkInterfaces.hasMoreElements()) {
            netIe = networkInterfaces.nextElement();
            if (netIe.isUp()) {
                Enumeration<InetAddress> addresses = netIe.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    addrList.add(addresses.nextElement());
                }
            }
        }
        return addrList;
    }
    protected DatagramSocket provideNewUdpSocket(int port) throws BulbBridgeHwException {
        if( this.udpSocket != null && !this.udpSocket.isClosed() ) return udpSocket;
        try {
            this.udpSocket = new DatagramSocket(port);
            this.udpSocket.setSoTimeout(SOCKET_TIMEOUT);
            return udpSocket;
        } catch (SocketException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }
    private boolean ignorePacket(DatagramPacket packet){
        return LOCAL_ADDRESSES.contains( packet.getAddress() );
    }

    private class UdpListenExcecutor implements Runnable {

        private final Integer SUSPENSION_TIMEOUT_MS = 1000;
        private final Map<LifxPacketType, Long> suspensionTime = new HashMap<>(3);
        private final Map<LifxPacketType, LifxMessage[]> suspendedMessages = new HashMap<>(10);

        private volatile ConcurrentHashMap<LifxPacketType, CompletableFuture<LifxMessage[]>> clientsWaiting;

        public UdpListenExcecutor(ConcurrentHashMap<LifxPacketType, CompletableFuture<LifxMessage[]>> clientsWaiting) {
            this.clientsWaiting = clientsWaiting;
        }

        @Override
        public void run() {
            log.debug("|-> Listen executor started.");
            while(!clientsWaiting.isEmpty()){
//                log.debug("Clients waiting before: {}", clientsWaiting.size());
                clientsWaiting.forEachKey(200, key -> suspensionTime.putIfAbsent(key, System.currentTimeMillis()));
//                log.debug("Clients waiting: {}", clientsWaiting.size());
//                log.debug("Listen for answers.. ");
                listenForNewMessage();
//                log.debug("Clients waiting: {}", clientsWaiting.size());
//                log.debug("Serving answers.. ");
                resolveSuspendedMessages();
//                log.debug("Clients waiting after: {}", clientsWaiting.size());
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            listening = false;
            this.clientsWaiting = null;
            log.debug("ListenerTask finished.");
        }

        private void listenForNewMessage(){
            try {
                Optional<LifxMessage> message = retrieveDatagramMessage();
                if(!message.isPresent()) return;
                LifxPacketType packetType = message.get().getType();
                // If not expected, we do not wait for such messages
//                if(!clientsWaiting.containsKey(packetType)) return;
                suspendedMessages.merge(packetType, new LifxMessage[]{message.get()},
                        (m1, m2) -> {
                            log.debug("MERGING result sets.");
                            suspensionTime.put(packetType, System.currentTimeMillis());
                            Set<LifxMessage> res = new HashSet<>(m1.length + m2.length);
                            res.addAll(Arrays.asList(m1));
                            res.addAll(Arrays.asList(m2));
                            LifxMessage[] resArr = new LifxMessage[res.size()];
                            return res.toArray(resArr);
                        }
                );
            } catch (BulbBridgeHwException e) {
                log.error("Error retrieving messages from udp socket", e);
            }
        }
        private void resolveSuspendedMessages() {
            final Set<LifxPacketType> resolvedPacketTypes = new HashSet<>(5);
            suspensionTime.forEach((packetType, time) -> {
                // For that kind of response type: As soon as we got one message, we resolve
                if (!packetType.isYieldsManyResponsePackets()) {
                    if (clientsWaiting.containsKey(packetType) && suspendedMessages.containsKey(packetType)) {
                        LifxMessage[] suspMsg = suspendedMessages.remove(packetType);
                        if (suspMsg != null && System.currentTimeMillis() - suspensionTime.get(packetType) < SUSPENSION_TIMEOUT_MS) {
                            clientsWaiting.remove(packetType).complete(suspMsg);
                            log.debug("Message resolved: {}", suspMsg);
                        }else{
                            clientsWaiting.remove(packetType).completeExceptionally(
                                    new BulbBridgeHwException("No answer received within time frame"));
                        }

                        resolvedPacketTypes.add(packetType);
                    }
                }
                // When timeout hits, no matter of packet type, we resolve for sure, be there a result or not
                if (System.currentTimeMillis() - suspensionTime.get(packetType) > SUSPENSION_TIMEOUT_MS) {
                    CompletableFuture<LifxMessage[]> result = clientsWaiting.remove(packetType);
                    if (result != null) { // Could be null in case it was resolved in previous block
                        LifxMessage[] suspMsg = suspendedMessages.remove(packetType);
                        if (suspMsg != null) {
                            result.complete(suspMsg);
                            log.debug("Message resolved: {}", suspMsg);
                        } else {
//                            log.debug("Hit message awaiting timeout {}", packetType);
                            result.completeExceptionally(new BulbBridgeHwException("No answer received within time frame"));
                        }
                    }else{
//                        log.debug("Dropping messages for type {}", packetType);
                        suspendedMessages.remove(packetType); // dismiss message having no client waiting
                    }
                    resolvedPacketTypes.add(packetType);
                }
            });
            resolvedPacketTypes.forEach( f -> suspensionTime.remove(f) );
        }
    }

}
