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

import javax.net.SocketFactory;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Thomas Wendzinski.
 */
public class BulbBridgeHardwareAdapter_LIFX implements BulbBridgeHardwareAdapter {

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFX.class);

    private final BulbCmdTranslator_LIFX cmdTranslator = new BulbCmdTranslator_LIFX();
    private final UdpLifxMessageTransportManager transportManager;

    private DatagramSocket udpSocket;
    private Socket tcpSocket;

    //~ ///////////////////////////////////////////////////////////////////////
    public BulbBridgeHardwareAdapter_LIFX() {
        this.transportManager = new UdpLifxMessageTransportManager();
    }

    //~ ///////////////////////////////////////////////////////////////////////

    private Socket provideNewTcpSocket(InetAddress addr, int port) throws BulbBridgeHwException {
        try {
            this.tcpSocket = SocketFactory.getDefault().createSocket(addr, port);
            this.tcpSocket.setSoTimeout(3000);
            return this.tcpSocket;
        } catch (IOException e) {
            throw new BulbBridgeHwException(e.getMessage(), e);
        }
    }


//    //FIXME make private!
//    public void sendDatagramMessage(LifxMessage message ) throws BulbBridgeHwException {
////        this.udpSocket = provideNewUdpSocket(LIFX_STD_PORT);
//        log.debug("|-- Going to send data on udpSocket address [{}} and port [{}]..", message.getAddress(), message.getPort());
//        log.debug(" -- " + message.toBytes());
//        DatagramPacket packet = new DatagramPacket(
//                message.toBytes(), message.toBytes().length, message.getAddress(), message.getPort()
//        );
//        try {
//            udpSocket.send(packet);
//        } catch (IOException e) {
//            throw new BulbBridgeHwException(e.getMessage(), e);
//        }
//    }
    //FIXME make private!
    public byte[] sendAndReceiveTcpMessage(LifxMessage message) throws BulbBridgeHwException {
        this.tcpSocket = provideNewTcpSocket(message.getAddress(), message.getPort());
        log.debug("|-- Going to send data on tcpSocket at address [{}} and port [{}]..", message.getAddress(), message.getPort());
        log.debug(" -- " + message);

        OutputStream outputStream = null;
        BufferedInputStream inFromServer = null;
        byte[] in = new byte[84]; // 32 + 52
        try {
            outputStream = tcpSocket.getOutputStream();
        } catch (IOException e) {
            log.error("Error opening output stream on tcp socket", e);
        }
        try {
            inFromServer = new BufferedInputStream(tcpSocket.getInputStream());
        } catch (IOException e) {
            log.error("Error opening input stream on tcp socket", e);
        }
        try {
            outputStream.write(message.toBytes());
        } catch (IOException e) {
            log.error("Error on write to tcp socket", e);
        }
//        try {
//            int res = inFromServer.read(in);
//            if(res != -1){
//                log.warn("Target input byte array too small, as there was data left to be read; {} bytes", res);
//            }
//        } catch (IOException e) {
//            log.error("Error on read from tcp socket", e);
//        }
        return in;

    }

    @Override
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException {
//        sendDatagramMessage(
//                new LifxMessage<>(
//                        LifxPacketType.REQ_PAN_GATEWAY, LifxMessagePayload.emptyPayload(),
//                        lanMulticastAddress(), LIFX_STD_PORT, null ) //TODO: Add constructor not expecting mac address
//        );
//
//        DatagramPacket datagramPacket = retrieveDatagramMessage();
//        LifxMessage lifxMessage = LifxMessage.fromBytes(datagramPacket.getData());
//        log.debug(":) -- ", lifxMessage.rawDataToString());
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
