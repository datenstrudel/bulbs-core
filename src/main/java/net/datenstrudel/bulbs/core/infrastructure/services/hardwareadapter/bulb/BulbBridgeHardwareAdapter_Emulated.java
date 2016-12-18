package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.bulb.HwResponse;
import net.datenstrudel.bulbs.core.domain.model.bulb.InvocationResponse;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Thomas Wendzinski
 */
@Component(value = "bulbBridgeHardwareAdapter_Emulated")
public class BulbBridgeHardwareAdapter_Emulated implements BulbBridgeHardwareAdapter{

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
            BulbsPlatform platform) throws BulbBridgeHwException {
        if(!knownBridges.containsKey(address)){
            log.info("|-- Creating new emulated bridge.");
            createEmulatedBridge(address, contextUserId);
        }
        log.info("|-- Serving emulated bridge.");
        return knownBridges.get(address);
    }

    @Override
    public CompletableFuture<InvocationResponse> discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        log.info("|-- NoOp.");
        BulbBridge bridge = this.knownBridges.get(address);
        Bulb newBulb = new Bulb(
                new BulbId(bridge.getId(), ""+bridge.getBulbs().size() + "1"),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_"+ (bridge.getBulbs().size() + 1) +"__" + bridge.getName(),
                bridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<>());
        bridge.getBulbs().add(newBulb);
        knownBulbs.get(address).add(newBulb);
        return CompletableFuture.completedFuture(new InvocationResponse("Emulated Search Started.", false));
    }

    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {
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
            BulbsPlatform platform) throws BulbBridgeHwException {
        log.info("|-- Returning principals directly created so far.");
        return knownBridgesPrincipals.get(bridge.getLocalAddress());
    }

    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbsPlatform platform) throws BulbBridgeHwException {
        log.info("Creating "+principal+" for emulated bridge["+address+"]");
        if(!this.knownBridgesPrincipals.containsKey(address)){
            knownBridgesPrincipals.put(address, new HashSet<>());
        }
        this.knownBridgesPrincipals.get(address).add(principal);
        return new InvocationResponse("Successfully created principal on emulated bridge.", false);
    }

    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove,
            BulbsPlatform platform) throws BulbBridgeHwException {
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
            BulbsPlatform platform) throws BulbBridgeHwException {
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
            BulbsPlatform platform) throws BulbBridgeHwException {
        
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
            BulbsPlatform platform) throws BulbBridgeHwException {
        
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
            BulbsPlatform platform) throws BulbBridgeHwException {
        log.info("Modifying bulb["+bulbId+"] attributes - noOp" );
        return new InvocationResponse("OK", false);
    }

    @Override
    public void applyBulbState(
            final BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbsPlatform platform, BulbState previousState) throws BulbBridgeHwException {
        log.info("Applying state["+state+"] emulated - noOp");
        
        Bulb bulb = Iterables.find(this.knownBulbs.get(address), new Predicate<Bulb>() {
            @Override
            public boolean apply(Bulb input) {
                return input.getId().sameValueAs(bulbId);
            }
        }, null);

        final Field stateField = ReflectionUtils.findField(Bulb.class, "state");
        ReflectionUtils.makeAccessible(stateField);
        ReflectionUtils.setField(stateField, bulb, state);
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
                new HashMap<>());
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
                new BulbId(parentBridge.getId(), "1"),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_1__" + parentBridge.getName(),
                parentBridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<>()));
        bulbs.add(new Bulb(
                new BulbId(parentBridge.getId(), "2"),
                BulbsPlatform._EMULATED, 
                "EmulatedBulbName_2__" + parentBridge.getName(),
                parentBridge, 
                new BulbState(new ColorRGB(0, 0, 0), true), 
                true, 
                new HashMap<>()));
        
        if(!knownBulbs.containsKey(parentBridge.getLocalAddress())){
            this.knownBulbs.put(parentBridge.getLocalAddress(), new HashSet<>());
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
