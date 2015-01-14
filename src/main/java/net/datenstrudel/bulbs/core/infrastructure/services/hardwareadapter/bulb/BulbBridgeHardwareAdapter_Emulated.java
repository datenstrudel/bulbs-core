package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value = "bulbBridgeHardwareAdapter_Emulated")
public class BulbBridgeHardwareAdapter_Emulated implements BulbBridgeHardwareAdapter<BulbCmdTranslator_HTTP>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_Emulated.class);
    private Map<BulbBridgeAddress, BulbBridge> knownBridges = new HashMap<>();
    private Map<BulbBridgeAddress, Set<BulbsPrincipal>> knownBridgesPrincipals = new HashMap<>();
    private Map<BulbBridgeAddress, Set<Bulb>> knownBulbs = new HashMap<>();
    
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbBridgeHardwareAdapter_Emulated() {}
        
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbBridge toBridge(
            final BulbBridgeAddress address, 
            BulbBridgeId bridgeId,
            BulbsPrincipal principal, 
            BulbsContextUserId contextUserId, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        if(!knownBridges.containsKey(address)){
            log.info("|-- Creating new emulated bridge.");
            createEmulatedBridge(address, contextUserId);
        }
        log.info("|-- Serving emulated bridge.");
        return knownBridges.get(address);
    }

    @Override
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("|-- NoOp.");
        BulbBridge bridge = this.knownBridges.get(address);
        Bulb newBulb = new Bulb(
                new BulbId(bridge.getId(), bridge.getBulbs().size() + 1),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_"+ (bridge.getBulbs().size() + 1) +"__" + bridge.getName(),
                bridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<String, Object>());
        bridge.getBulbs().add(newBulb);
        knownBulbs.get(address).add(newBulb);
        return new InvocationResponse("Emulated Search Started.", false);
    }

    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        if(!knownBridges.containsKey(address)){
            log.warn("Creating bridge without consistent contextUserId. Application failures might occur.");
            createEmulatedBridge(address, null);
        }
        log.info("|-- Modifying bridge attributes..");
        knownBridges.get(address).getBridgeAttributes().putAll(attributes);
        return new HwResponse(null, HttpStatus.OK);
    }

    @Override
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge, 
            BulbsPrincipal principal,
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("|-- Returning principals directly created so far.");
        return knownBridgesPrincipals.get(bridge.getLocalAddress());
    }

    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("Creating "+principal+" for emulated bridge["+address+"]");
        if(!this.knownBridgesPrincipals.containsKey(address)){
            knownBridgesPrincipals.put(address, new HashSet<BulbsPrincipal>());
        }
        this.knownBridgesPrincipals.get(address).add(principal);
        return new InvocationResponse("Successfully created principal on emulated bridge.", false);
    }

    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("Retrieving emulated principal" + principal2Remove);
        if(this.knownBridgesPrincipals.containsKey(address)){
            knownBridgesPrincipals.get(address).remove(principal2Remove);
        }
        return new HwResponse(null, HttpStatus.OK);
    }

    @Override
    public BulbId[] toBulbIds(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("Retrieving emulated bulbs' IDs for bridge["+parentBridge+"]");
        if(!this.knownBulbs.containsKey(parentBridge.getLocalAddress())){
            createEmulatedBulbs(parentBridge);
        }
        BulbId[] res = new BulbId[this.knownBulbs.get(parentBridge.getLocalAddress()).size()];
        int i = 0;
        for (Bulb el : this.knownBulbs.get(parentBridge.getLocalAddress())) {
            res[i++] = el.getId();
        }
        return res;
    }

    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        
        log.info("Retrieving emulated bulbs for bridge["+parentBridge+"]");
        if(!this.knownBulbs.containsKey(parentBridge.getLocalAddress())){
            createEmulatedBulbs(parentBridge);
        }
        log.info("|-- Retrieving emulated bulbs.");
        Bulb[] res = new Bulb[knownBulbs.get(parentBridge.getLocalAddress()).size()];
        int i = 0;
        for(Bulb el : knownBulbs.get(parentBridge.getLocalAddress())){
            res[i++] = el.copy();
        }
        return res;
    }

    @Override
    public Bulb toBulb(
            final BulbId bulbId, 
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        
        log.info("Retrieving emulated bulb["+bulbId+"]");
        if(!this.knownBulbs.containsKey(parentBridge.getLocalAddress())){
            createEmulatedBulbs(parentBridge);
        }
        Bulb bulb = Iterables.find(
                this.knownBulbs.get(parentBridge.getLocalAddress()), 
                new Predicate<Bulb>() {
            @Override
            public boolean apply(Bulb input) {
                return input.getId().sameValueAs(bulbId);
            }
        }, null);
//        bulb = bulb.copy();
        
        return bulb.copy();
    }

    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("Modifying bulb["+bulbId+"] attributes - noOp" );
        return new InvocationResponse("OK", false);
    }

    @Override
    public InvocationResponse applyBulbState(
            final BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbState state, 
            BulbCmdTranslator_HTTP cmdTranslator) throws BulbBridgeHwException {
        log.info("Applying state["+state+"] emulated - noOp");
        
        Bulb bulb = Iterables.find(this.knownBulbs.get(address), new Predicate<Bulb>() {
            @Override
            public boolean apply(Bulb input) {
                return input.getId().sameValueAs(bulbId);
            }
        }, null);
        
        ReflectionTestUtils.setField(bulb, "state", state);
        
        return new InvocationResponse("OK", false);
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void createEmulatedBridge(
            final BulbBridgeAddress address, 
            BulbsContextUserId contextUserId ){
        BulbBridge res = new BulbBridge(
                new BulbBridgeId( UUID.randomUUID().toString() ),
                "emulated_mac__" + emulatedRandomMac(),
                BulbsPlatform._EMULATED, 
                address, 
                "EmulatedBridgeName__" + address.toHttpAddress(), 
                contextUserId, 
                new HashMap<String, Object>());
        res.getBridgeAttributes().put("name", "EmulatedBridgeName__" + address.toHttpAddress());
        res.setOnline(true);
        knownBridges.put(address, res);
    }

    private void createEmulatedBulbs(
            final BulbBridge parentBridge
            ){
        log.info(" -- Creating emulated bulbs for " + parentBridge);
        Set<Bulb> bulbs = new HashSet<>();
        
        bulbs.add(new Bulb(
                new BulbId(parentBridge.getId(), 1),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_1__" + parentBridge.getName(),
                parentBridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<String, Object>()));
        bulbs.add(new Bulb(
                new BulbId(parentBridge.getId(), 2),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_2__" + parentBridge.getName(),
                parentBridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<String, Object>()));
        
        if(!knownBulbs.containsKey(parentBridge.getLocalAddress())){
            this.knownBulbs.put(parentBridge.getLocalAddress(), new HashSet<Bulb>());
        }
        this.knownBulbs.get(parentBridge.getLocalAddress()).addAll(bulbs);
    }
    
    private String emulatedRandomMac(){
        String res = "";
        for (int i = 0; i < 6; i++) {
            res += "" + (new Double(Math.random() * 10).intValue()) + ":";
        }
        return res.substring(0, res.length()-1);
    }
}
