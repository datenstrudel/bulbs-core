package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

/**
 * Created by Thomas Wendzinski.
 */
public class UdpLifxMessageTransportManager {

    public static final Logger log = LoggerFactory.getLogger(UdpLifxMessageTransportManager.class);
    public static final int LIFX_STD_PORT = 56700;
    public static final int SOCKET_TIMEOUT = 3000;

    private final List<InetAddress> LOCAL_ADDRESSES;
    private final ConcurrentHashMap<LifxPacketType, CompletableFuture<LifxMessage[]>> clientsWaiting
            = new ConcurrentHashMap<>(5);

    private DatagramSocket udpSocket;
    private FutureTask udpMessageListenerTask;

    public UdpLifxMessageTransportManager() {
        try{
            this.LOCAL_ADDRESSES = allLocalAddresses();
        }catch (SocketException e) {
            throw new IllegalStateException("Cannot construct due to local addresses not resolvable", e);
        }

    }
    @PostConstruct
    public void init() {
        StringJoiner joiner = new StringJoiner(" ; ");
        this.LOCAL_ADDRESSES.stream().forEach( a -> joiner.add(a.getHostAddress()));
        log.info("Local adresses found: {}", joiner.toString());

        udpMessageListenerTask = new FutureTask<Void>( ( () -> listenForAnswers()) , null);
//        try {
//            udpMessageListenerTask.wait();
//        } catch (InterruptedException e) {
//            log.warn("Error on starting UdpLifxMessageTransportManager, due to the msg listener task couldn't be interrupted");
//        }
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
        this.udpMessageListenerTask.notify();
    }

    private void sendDatagramMessage(LifxMessage message ) throws BulbBridgeHwException {
        if(log.isDebugEnabled()){
            log.debug("|-- Going to send data on udpSocket address [{}} and port [{}]..", message.getAddress(), message.getPort());
            log.debug(" -- " + message.toBytes());
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
        byte[] respPayload = new byte[64];
        DatagramPacket destPacket = new DatagramPacket(respPayload, respPayload.length);
        try {

            this.udpSocket.receive(destPacket);
            LifxMessage res = LifxMessage.fromBytes(
                    destPacket.getData(), destPacket.getAddress(), destPacket.getPort());

            if( ignorePacket(destPacket) ){
                // FIXME Prevent potential endless recursion!!
                log.debug("Message has been discarded from address: {}", destPacket.getAddress());
                return retrieveDatagramMessage();
            }
            log.debug(":) -- Retrieved packet from address [{}]", destPacket.getAddress());
            return Optional.of(res);
        }catch(SocketTimeoutException e){
            log.info(":( .. receive timeout..");
            return Optional.empty();
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }

    }

    private DatagramSocket provideNewUdpSocket(int port) throws BulbBridgeHwException {
        if( !this.udpSocket.isClosed() && this.udpSocket.isConnected() ) return udpSocket;
        try {
            this.udpSocket = new DatagramSocket(port);
            this.udpSocket.setSoTimeout(SOCKET_TIMEOUT);
            return udpSocket;
        } catch (SocketException e) {
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
    private boolean ignorePacket(DatagramPacket packet){
        return LOCAL_ADDRESSES.contains( packet.getAddress() );
    }

    private void listenForAnswers(){
        while(clientsWaiting.size() > 0){
            try {
                Optional<LifxMessage> message = retrieveDatagramMessage();
                if(!message.isPresent()) continue;
                CompletableFuture<LifxMessage[]> result = clientsWaiting.get(message.get().getType());
                // FIXME: Make sure to catch _several_ messages of the same type
                result.complete(new LifxMessage[]{message.get()});

            } catch (BulbBridgeHwException e) {
                log.error("Error retrieving messages from udp socket", e);
            }
        }
        try {
            this.udpMessageListenerTask.wait();
        } catch (InterruptedException e) {
            log.warn("Error suspending message listener task: {}", e.getMessage());
        }
    }

}
