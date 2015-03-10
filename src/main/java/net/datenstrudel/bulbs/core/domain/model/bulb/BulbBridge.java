package net.datenstrudel.bulbs.core.domain.model.bulb;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbBridgeSynced;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a communication bridge to which a set of bulbs might be paired.
 * For some implementation(s) (like LiveX) one bulb actually has the role of the
 * bridge.
 * @author derTom
 * @version 1.0
 * @updated 08-Jun-2013 22:54:55
 */
public class BulbBridge extends Entity<BulbBridge, BulbBridgeId> {

    //~ Member(s) //////////////////////////////////////////////////////////////
//    private static final Logger log = LoggerFactory.getLogger(BulbBridge.class);
    private BulbsPlatform platform;
	private Map<String,Object> bridgeAttributes = new HashMap<>();
    private String macAddress;
    private BulbBridgeAddress localAddress;
    private BulbBridgeAddress wanAddress;
    private String name;
    private boolean online = false;
    
	private BulbsContextUserId owner;
	/**
	 * Bulbs paired with this bridge
	 */
	private Set<Bulb> bulbs = new HashSet<>();
	
    //~ Service Stuff
    @Transient
    private BulbsHwService hardwareService;
    
    //~ Construction ///////////////////////////////////////////////////////////
	public BulbBridge(){
        this.hardwareService = DomainServiceLocator.getBean(BulbsHwService.class);
	}
    /**
     * Constructor for Hardware translator
     * @param bridgeId
     * @param macAddress
     * @param platform
     * @param localAddress
     * @param name
     * @param owner
     * @param bridgeAttributes 
     */
    public BulbBridge(
            BulbBridgeId bridgeId, 
            String macAddress,
            BulbsPlatform platform, 
            BulbBridgeAddress localAddress, 
            String name, 
            BulbsContextUserId owner,
            Map<String,Object> bridgeAttributes) {
        super(bridgeId);
        this.hardwareService = DomainServiceLocator.getBean(BulbsHwService.class);
//        this.bridgeId = bridgeId;
        this.macAddress = macAddress;
        this.platform = platform;
        this.localAddress = localAddress;
        this.name = name;
        this.owner = owner;
        this.bridgeAttributes = bridgeAttributes;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getMacAddress() {
        return macAddress;
    }
    
    public BulbsPlatform getPlatform() {
        return platform;
    }
    public Map<String, Object> getBridgeAttributes() {
        return bridgeAttributes;
    }
    public BulbsContextUserId getOwner() {
        return owner;
    }
    /**
     * The unique name of that bridge
     * @return 
     */
    public String getName(){
		return this.name;
	}
    public boolean isOnline() {
        return online;
    }
    //~ BRIDGE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
	 * The address assigned within the local network <code>this</code> lives.
     * 
     * @return 
     */
	public BulbBridgeAddress getLocalAddress(){
		return this.localAddress;
	}
    /**
     * @return The (optional) address that identifies the bridge over a WAN
     */
	public BulbBridgeAddress getWanAddress(){
		return wanAddress;
	}
    
    /**
	 * Modify the address of the underlying Hardware-Interface within this internal
	 * model (only).
	 * 
     * @param address
     * @param principal  
     * @throws net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException
	 */
	public void modifyLocalAddress(BulbBridgeAddress address, BulbsPrincipal principal) throws BulbBridgeHwException{
        BulbBridge testBridge = hardwareService.bridgeFromHwInterface(
                address, super.getId(), principal, owner, platform);
        if(testBridge == null){
            throw new IllegalArgumentException("No Bridge found on new address["+address+"]");
        }
        if(!testBridge.getMacAddress().equals(this.macAddress)){
            throw new IllegalArgumentException("Cannot change localAddress due to bridge on new address has different MAC address!");
        }
        this.localAddress = address;
        syncToHardwareState(principal);
	}
    /**
	 * @param name
     * @param principal
     * @throws BulbBridgeHwException  
	 */
	public void modifyName(String name, BulbsPrincipal principal) throws BulbBridgeHwException{
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put("name", name);
        hardwareService.modifyBridgeAttributes(this.localAddress, principal, attrMap, platform);
        setName(name);
	}
    /**
      * Register a BulbsPrincipal against this BulbBridge. Usually link button 
      * must be pressed.<br /><br />
      * This operation won't add the new principal to the user. 
      * Instead an event will be trigged that eventually will result in
      * the creation of the <code>principal2Register</code> through the according 
      * user application service.
      * 
      * @param principal2Register
      * @param user
      * @throws BulbBridgeHwException  
	 */
	public void registerBulbsPrincipal(
            BulbsPrincipal principal2Register, 
            BulbsContextUser user) throws BulbBridgeHwException{
        assert(principal2Register.getAppId() != null);
        assert(!StringUtils.isEmpty(principal2Register.getUsername()));
        assert(!StringUtils.isEmpty(principal2Register.getBulbBridgeId()));
        assert( new BulbBridgeId(principal2Register.getBulbBridgeId())
                .sameValueAs(super.getId()) );
        if(!this.owner.sameValueAs(user.getBulbsContextUserlId())){
            throw new IllegalArgumentException("Illegal access: User["+user.getBulbsContextUserlId()
                    + "] not owner of bridge that was attempted of beeing modified.");
        }
        hardwareService.createBulbsPrincipal(
                this.localAddress, principal2Register, platform);
        principal2Register.setState(BulbsPrincipalState.PENDING);
       
        DomainEventPublisher.instance().publish(
                new BulbsPrincipalInitLinkEstablished(
                    user.getBulbsContextUserlId().getUuid(),
                    principal2Register));
	}
	public void removeBulbsPrincipal(
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbsContextUser user) throws BulbBridgeHwException{
        assert(!principal.sameValueAs(principal));
        hardwareService.removeBulbsPrincipal(localAddress, principal, principal2Remove, platform);
        
        DomainEventPublisher.instance().publish(
                new BulbsPrincipalDeletedFromBridge(principal2Remove, user.getBulbsContextUserlId().getUuid())
                );
    }

    /**
	 * Mirror the state of the hardware into <code>this</code> and its associated bulbs
	 * 
     * @param principal
     * @throws BulbBridgeHwException  
	 */
	public void syncToHardwareState(BulbsPrincipal principal) throws BulbBridgeHwException{
//        if(log.isDebugEnabled()){
//            log.debug(this.bridgeId + " - " + name +": " + "Synching to hardware state");
//        }
        //~ Retrieve bridge parameter
        BulbBridge refBridge = hardwareService.bridgeFromHwInterface(
                localAddress, getId(), principal, owner, platform);
        if(!refBridge.getMacAddress().equals(this.macAddress)){
            throw new IllegalStateException(
                    "Reference Hardware Bridge[" + refBridge.getId()
                    + "] not equal to this[" + getId() + "], due to MAC addresses do not match!");
        }
        setBridgeAttributes( refBridge.getBridgeAttributes() );
        setLocalAddress(refBridge.getLocalAddress());
        setName(refBridge.getName());
        setOnline(true); // Just received information from hardware
        //~ Retrieve bulbs
        Bulb[] refBulbs = this.hardwareService
                .bulbsFromHwInterface(this, principal, platform);
        Bulb bulbOrig;
        BulbId[] bulbIdsRef = new BulbId[refBulbs.length];
        int i = 0;
        for (Bulb bulbRef : refBulbs) {
            bulbIdsRef[i++] = bulbRef.getId();
            bulbOrig = bulbById(bulbRef.getId());
            if(bulbOrig != null){
                // Update existing
                bulbOrig.syncToHardwareState(bulbRef);
            }else{
                // Append new
                this.addBulb(bulbRef);
            }
        }
        // Maintain online status of known bulbs
        this.updateBulbsOnlineState(bulbIdsRef);
        DomainEventPublisher.instance().publish(
                new BulbBridgeSynced(getId().getUuId(), principal));
	}

    /**
     * This bridge couldn't be reached over net; This method sets any
     * associated artifacts offline and emits a {@link net.datenstrudel.bulbs.core.domain.model.bulb.events.BulbBridgeSynced} event.
     */
    public void markRecursivelyOffline(BulbsPrincipal principal) {
        setOnline(false);
        this.bulbs.forEach( b -> b.changedOnlineState(false));
        DomainEventPublisher.instance().publish(
                new BulbBridgeSynced(getId().getUuId(), principal));
    }

    //~ BULBS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Set<Bulb> getBulbs() {
        return bulbs;
    }

    public Bulb bulbById(final BulbId bulbId){
        return Iterables.find(bulbs, new Predicate<Bulb>() {
            @Override
            public boolean apply(Bulb t) {
                return t.getId().sameValueAs(bulbId);
            }
        }, null);
    }
    public Bulb bulbByName(final String name){
        return Iterables.find(bulbs, new Predicate<Bulb>() {
            @Override
            public boolean apply(Bulb t) {
                return t.getName().equals(name);
            }
        }, null);
    }
    public Set<String> allBulbNames(){
        Set<String> res = new HashSet<>();
        for (Bulb bulb : bulbs) {
            res.add(bulb.getName());
        }
        return res;
    }
    
    /**
     * Performs a search for new bulb(s) to be registered for this bridge.
     * 
     * @param principal
     * @throws BulbBridgeHwException  
	 */
	public void performBulbSearch(BulbsPrincipal principal) throws BulbBridgeHwException{
		this.hardwareService.discoverNewBulbs(
                getId(), this.localAddress, principal, this.platform);
	}
    /**
	 * 
     * @param command
     * @param principal 
     * @throws BulbBridgeHwException  
	 */
	public void execBulbActuation(
            BulbActuatorCommand command, 
            BulbsPrincipal principal) throws BulbBridgeHwException{
        if(command.getTargetId() == null) throw new IllegalArgumentException("BulbActuatorCommand.targetId must not be null!");
        BulbId bulbId = command.getTargetId();
        Bulb b = bulbById(bulbId);
        if(b == null) throw new IllegalArgumentException("Bulb["+bulbId+"] doesn't exist!");
        
        b.execBulbActuation(command, principal);
	}
    public void cancelActuation(
            ActuationCancelCommand cancelCommand,
            BulbsPrincipal principal
            ){
        
        if(cancelCommand.getEntityIds() == null) 
            throw new IllegalArgumentException("Entity IDs must not be null!");
//        log.info("Bridge["+this.name+"] cancel Actuation by Ids: " + cancelCommand.getEntityIds());
        cancelCommand.getEntityIds().stream().forEach( (BulbId bId) -> {
            Bulb b = bulbById(bId);
            if(b == null) {
//                log.error("Bulb["+bId+"] doesn't exist!");
                return ;
            }
            b.cancelActuation(cancelCommand);
        } );
        
    }
    
    /**
     * Remove a bulb from the model that is not known anymore to the underlying 
     * hardware bridge.
     * @param bulb to be removed
     */
    public void forgetBulb(Bulb bulb){
        assert(bulb.getBridge().sameIdentityAs(this));
        bulb.setBridge(null);
        this.bulbs.remove(bulb);
    }
    /**
	 * Modify a bulb's _unique_ name.
	 * 
     * @param principal 
     * @param bulbId
     * @param newName
     * @throws BulbBridgeHwException  
     * @throws  IllegalArgumentException in case either <code>bulbId</code> doesn't exist
     * or a Bulb with <code>newName</code> does already exist.
	 */
	public void mofiyBulbName(BulbsPrincipal principal, BulbId bulbId, String newName)
            throws BulbBridgeHwException{
        Bulb b = bulbById(bulbId);
        Bulb duplicate = bulbByName(newName);
        
        if(b == null) 
            throw new IllegalArgumentException("Bulb["+bulbId+"] doesn't exist!");
        if(duplicate != null) 
            throw new IllegalArgumentException(
                    "New name[" + newName + "] provided not unique within bridge!");
        b.modifyName(newName, principal);
	}
    
    public void updateBulbsOnlineState(BulbsPrincipal principal) throws BulbBridgeHwException{
        BulbId[] _idsRef = hardwareService.bulbIdsFromHwInterface(this, principal, platform);
        updateBulbsOnlineState(_idsRef);
    }
    protected void updateBulbsOnlineState(BulbId[] idsFoundRecently) throws BulbBridgeHwException{
        if(idsFoundRecently == null || idsFoundRecently.length == 0) return;
        Set<BulbId> idsRef = Sets.newHashSet(idsFoundRecently);
        for (Bulb el : this.bulbs) {
            if(idsRef.contains(el.getId())){
//                el.changedOnlineState(true); // that the bridge returns the bulb doesn't mean it is actually reachable and online
            }else{
                el.changedOnlineState(false);
            }
        }
    }
    
    private void addBulb(Bulb newBulb){
        Assert.isTrue(newBulb.getBridge().sameIdentityAs(this));
        this.bulbs.add(newBulb);
        
    }
    //~PRESETs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
	 * 
	 * @param state
	 * @param principal
	 */
	public void applyPreset(Preset state, BulbsPrincipal principal){
        throw new UnsupportedOperationException("Not supported yet!");
	}
    
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean sameIdentityAs(BulbBridge other) {
        if(other == null)return false;
        return this.getId().sameValueAs(other.getId());
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BulbBridge other = (BulbBridge) obj;
        return this.sameIdentityAs(other);
    }
    @Override
    public String toString() {
        return "BulbBridge{" 
                + "bridgeId=" + getId()
                + ", platform=" + platform 
//                + ", bridgeAttributes=" + bridgeAttributes 
                + ", localAddress=" + localAddress 
                + ", wanAddress=" + wanAddress 
                + ", name=" + name 
                + ", online=" + online 
                + ", owner=" + owner 
                + ", bulbs=" + bulbs
                + ", hardwareService=" + hardwareService 
            + '}';
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    public void setBridgeAttributes(Map<String, Object> bridgeAttributes) {
        this.bridgeAttributes = bridgeAttributes;
    }
    public void setBulbs(Set<Bulb> bulbs) {
        this.bulbs = bulbs;
    }
//    public void setHardwareService(BulbsHwService hardwareService) {
//        this.hardwareService = hardwareService;
//    }
    public void setOnline(boolean online) {
        this.online = online;
    }

    private void setPlatform(BulbsPlatform platform) {
        this.platform = platform;
    }
    private void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    private void setLocalAddress(BulbBridgeAddress localAddress) {
        this.localAddress = localAddress;
    }
    private void setWanAddress(BulbBridgeAddress wanAddress) {
        this.wanAddress = wanAddress;
    }
    private void setName(String name) {
        this.name = name;
    }
    private void setOwner(BulbsContextUserId owner) {
        this.owner = owner;
    }

    public void postLoad(){
        this.bulbs.stream().forEach( b -> b.setBridge(this) );
    }

}