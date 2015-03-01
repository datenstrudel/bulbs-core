package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.PowerStatePayload;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class BulbBridgeHardwareAdapter_LIFX implements BulbBridgeHardwareAdapter {

    public static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_LIFX.class);

    private final BulbCmdTranslator_LIFX cmdTranslator = new BulbCmdTranslator_LIFX();
    private final UdpLifxMessageTransportManager transportManager;


    //~ ///////////////////////////////////////////////////////////////////////
    @Autowired
    public BulbBridgeHardwareAdapter_LIFX(UdpLifxMessageTransportManager transportManager) {
        this.transportManager = transportManager;
    }

    //~ ///////////////////////////////////////////////////////////////////////
    @Override
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException {

        CompletableFuture<LifxMessage[]> completableFuture = transportManager.sendAndReceiveAggregated(
                LifxMessage.messageFrom(
                        LifxMessagePayload.emptyPayload(LifxMessageType.REQ_PAN_GATEWAY),
                        address.toBroadcastAddress(), address.getPort()
                ), LifxMessageType.RESP_PAN_GATEWAY
        );

        LifxMessage[] messages = readFromFuture(completableFuture);
        return cmdTranslator.bridgeFromPayload(messages[0], bridgeId, address, contextUserId);
    }
    @Override
    public CompletableFuture<InvocationResponse> discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        return CompletableFuture.completedFuture(new InvocationResponse("Not supported by Lifx", false));
    }
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {
        throw new UnsupportedOperationException("Implement me!");
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

        Bulb[] bulbs = toBulbs(parentBridge, principal, platform);
        BulbId[] res = new BulbId[bulbs.length];
        for (int i = 0; i < bulbs.length; i++) {
            res[i] = bulbs[i].getId();
        }
        return res;
    }
    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {

        BulbBridgeAddress address = parentBridge.getLocalAddress();
        CompletableFuture<LifxMessage[]> completableFuture = transportManager.sendAndReceiveAggregated(
                LifxMessage.messageFrom(
                        LifxMessagePayload.emptyPayload(LifxMessageType.REQ_LIGHT_STATE),
                        address.toInetAddress(), address.getPort(), address.macAddress().get()),
                        LifxMessageType.RESP_LIGHT_STATE
        );

        LifxMessage[] messages = readFromFuture(completableFuture);
        List<Bulb> bulbs = new ArrayList<>(messages.length);
        for (LifxMessage message : messages) {
            bulbs.add(cmdTranslator.bulbFromPayload(
                    message, parentBridge,
                    new BulbId(parentBridge.getId(), message.getTarget_mac_address().toString()) ) );
        }
        Bulb[] res = new Bulb[bulbs.size()];
        bulbs.toArray(res);
        return res;
    }

    @Override
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {

        LifxMessage messageOut = cmdTranslator.toModifyBulbAttributesCmd(bulbId, address, principal, attributes);
        transportManager.send(
                messageOut
        );
        return new InvocationResponse("", false);
    }

    @Override
    public void applyBulbState(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbsPlatform platform,
            BulbState previousState) throws BulbBridgeHwException {

        LifxMessage messageOutColor = cmdTranslator.toApplyBulbStateCmd(bulbId, address, principal, state);
        LifxMessage messageOutPower = null;

        if(previousState == null || state.getEnabled() != previousState.getEnabled()){
            PowerStatePayload pwrPayload = PowerStatePayload.newModificationCommand(powerFromBulbstate(state));
            messageOutPower = LifxMessage.messageFrom(
                    pwrPayload, address.toInetAddress(), address.getPort(),
                    MacAddress.fromString(address.macAddress().get()),
                    MacAddress.fromString(bulbId.getLocalId())
            );
        }

        if(messageOutPower != null) transportManager.send(messageOutPower);
        if(messageOutColor != null) transportManager.send(messageOutColor);
    }

    private PowerStatePayload.Power powerFromBulbstate(BulbState state) {
        return state.getEnabled() ? PowerStatePayload.Power.ON : PowerStatePayload.Power.OFF;
    }

    public LifxMessage[] readFromFuture(CompletableFuture<LifxMessage[]> in) throws BulbBridgeHwException {
        try {
            return in.get();
        }catch (ExecutionException e) {
            if(e.getCause() instanceof BulbBridgeHwException){
                throw (BulbBridgeHwException) e.getCause();
            }else {
                throw new BulbBridgeHwException(e.getMessage(), e);
            }
        }catch(InterruptedException e) {
            throw new BulbBridgeHwException(e.getMessage());
        }
    }
}
