package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbBridgeHardwareAdapter_LIFX implements BulbBridgeHardwareAdapter {

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFX.class);
    public static final int LIFX_STD_PORT = 56700;
    private final BulbCmdTranslator_LIFX cmdTranslator = new BulbCmdTranslator_LIFX();
    private final List<InetAddress> LOCAL_ADDRESSES;

    public BulbBridgeHardwareAdapter_LIFX() {
        try{
            this.LOCAL_ADDRESSES = allLocalAddresses();
            StringJoiner joiner = new StringJoiner(" ; ");

            this.LOCAL_ADDRESSES.stream().forEach( a -> joiner.add(a.getHostAddress()));
            log.info("Local adresses found: {}", joiner.toString());
        }catch (SocketException e) {
            throw new IllegalStateException("Cannot construct due to local addresses not resolvable", e);
        }

    }

    private DatagramSocket socket;

    private DatagramSocket provideNewSocket() throws BulbBridgeHwException {
        try {
            this.socket = new DatagramSocket(LIFX_STD_PORT);
            this.socket.setSoTimeout(3000);
            return socket;
        } catch (SocketException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }
    private InetAddress lanMulticastAddress() throws BulbBridgeHwException {
        try {
            return InetAddress.getByName("192.168.1.255");
        } catch (UnknownHostException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }
    private InetAddress localhostAddress() throws BulbBridgeHwException {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            log.debug("Defined localhost as: {}", localHost.getHostAddress() );
            return localHost;
        } catch (UnknownHostException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }

    //FIXME make private!
    public void sendDatagramMessage(LifxMessage message, InetAddress target, int port ) throws BulbBridgeHwException {
//        this.socket = provideNewSocket();
        log.debug("|-- Going to send data on socket address [{}} and port [{}]..", target, port);
        log.debug(" -- " + message.toBytes());
        DatagramPacket packet = new DatagramPacket(
                message.toBytes(), message.toBytes().length, target, port
        );
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }

    /**
     * Retrieve a UDP message and discard messages from known unlikely senders
     * @return
     * @throws BulbBridgeHwException
     */
    //FIXME make private!
    public DatagramPacket retrieveDatagramMessage() throws BulbBridgeHwException {
        byte[] respPayload = new byte[64];
        DatagramPacket destPacket = new DatagramPacket(respPayload, respPayload.length);
        try {
            this.socket.receive(destPacket);
            // TODO: Discard from unlikely sender(s)
            if( ignorePacket(destPacket) ){
                // FIXME Prevent potential endless recursion!!
                log.debug("Message has been discarded from address: {}", destPacket.getAddress());
                return retrieveDatagramMessage();
            }

            log.debug(":) -- Retrieved packet from address [{}]", destPacket.getAddress());
            return destPacket;

        }catch(SocketTimeoutException e){
            log.info(":( .. receive timeout..");
            return null;
//            throw new BulbBridgeHwException(e.getMessage(), e);
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }

    }

    private boolean ignorePacket(DatagramPacket packet){
        return LOCAL_ADDRESSES.contains( packet.getAddress() );
    }

    private List<InetAddress> allLocalAddresses() throws SocketException {
        List<InetAddress> addrList = new LinkedList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();;
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

    @Override
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException {
        sendDatagramMessage(
                new LifxMessage(LifxPacketType.REQ_PAN_GATEWAY,
                        new LifxMessagePayload(new byte[0]) {

                        }),
                lanMulticastAddress(), LIFX_STD_PORT);

        DatagramPacket datagramPacket = retrieveDatagramMessage();
        LifxMessage lifxMessage = LifxMessage.fromBytes(datagramPacket.getData());
        log.debug(":) -- ", lifxMessage.rawDataToString());
        return null;
    }
    @Override
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }
    @Override
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public BulbId[] toBulbIds(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return new BulbId[0];
    }

    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return new Bulb[0];
    }

    @Override
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }

    @Override
    public InvocationResponse applyBulbState(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return null;
    }
}
