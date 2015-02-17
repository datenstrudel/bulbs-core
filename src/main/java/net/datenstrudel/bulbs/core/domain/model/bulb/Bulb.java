package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a bulb. Bulb is acore entity to be controlled by this
 * framework.
 */
public class Bulb extends Entity<Bulb, BulbId> implements Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(Bulb.class);
    private BulbsPlatform platform;
    private String name;
	private Map<String, Object> bulbAttributes = new HashMap<>();
	private BulbState state;
	@Transient
    private BulbBridge bridge;
    private Boolean online;
    
    private PriorityCoordinator priorityCoordinator = new PriorityCoordinator();

    //~ Service stuff
    @Transient
    private final BulbsHwService bulbsHwService;
    
    //~ Construction ///////////////////////////////////////////////////////////
	protected Bulb(){
        this.bulbsHwService = DomainServiceLocator.getBean(BulbsHwService.class);
    }
    public Bulb(
            BulbId id,
            BulbsPlatform platform, 
            String name, 
            BulbBridge bridge,
            BulbState state, 
            boolean online,
            Map<String,Object> attributes){
        this();
        this.id = id;
        this.platform = platform;
        this.name = name;
        this.bridge = bridge;
        this.state = state;
        this.online = online;
        this.bulbAttributes = attributes;
    }
    public Bulb(
            BulbId id,
            BulbsPlatform platform, 
            String name, 
            BulbBridge bridge,
            BulbState state, 
            boolean online,
            Map<String,Object> attributes,
            PriorityCoordinator priorityCoordinator){
        this();
        this.id = id;
        this.platform = platform;
        this.name = name;
        this.bridge = bridge;
        this.state = state;
        this.online = online;
        this.bulbAttributes = attributes;
        this.priorityCoordinator = priorityCoordinator;
    }
    public Bulb copy(){
        return new Bulb(id, platform, name, bridge, state, online, bulbAttributes, priorityCoordinator);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbBridge getBridge() {
        return bridge;
    }
    public BulbsPlatform getPlatform() {
        return platform;
    }
    public Boolean getOnline() {
        return online;
    }
    protected void changedOnlineState(boolean newState){
        this.setOnline(newState);
        // ToDo: Fire event!
    }
    public String getName(){
        return this.name;
    }
	public BulbState getState() {
        return state;
    }
    /**
     * Supposed to be called by internal system in order to sync system-known 
     * updated bulb state into domain model.
     * 
     * @param updatedState 
     */
    public void updateState(BulbState updatedState){
        this.state = this.state.mergeUpdated(updatedState);
    }
	/**
	 * Retrieve vendor specific attributes/ properties of this Bulb, for example type,
	 * name, modelId, software version, etc.
     * @return 
     */
	public Map<String,Object> getBulbAttributes(){
		return bulbAttributes;
	}
    public PriorityCoordinator getPriorityCoordinator() {
        return priorityCoordinator;
    }
	
	/**
     * 
     * @param key
     * @param value
     * @param principal 
     * @throws BulbBridgeHwException in case a hardware interaction error occurs
	*/
	protected void modifyAttribute(String key, Object value, BulbsPrincipal principal) throws BulbBridgeHwException{
        if(key.equalsIgnoreCase("name"))throw new IllegalArgumentException("Name must be modified by concerning method!");
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put(key, value);
        
        bulbsHwService.modifyBulbAttributes(
                id, bridge.getLocalAddress(), principal, attrMap, platform);
        
        //ToDo: Publish event (propably not here, but in concrete upstream business rule/ requirement.. )!
	}
	/**
	 * 
     * @param name the new unique name of <code>this</code>
     * @param principal 
     * @throws BulbBridgeHwException  
	 */
	protected void modifyName(String name, BulbsPrincipal principal) throws BulbBridgeHwException{
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put("name", name);
        
        bulbsHwService.modifyBulbAttributes(
                id, bridge.getLocalAddress(), principal, attrMap, platform);
        setName(name);
        
        //ToDo: Publish event!
	}
    
    /**
	 * Immediately apply the <code>cmd</code> to this bulb and the hardware
	 * <code>this</code> represents.
	 * 
     * @param cmd
     * @param principal 
     * @throws BulbBridgeHwException  
	 */
	protected void execBulbActuation(BulbActuatorCommand cmd, BulbsPrincipal principal) throws BulbBridgeHwException{
        if(!this.priorityCoordinator.executionAllowedFor(cmd)) return;
        bulbsHwService.executeBulbActuation(bridge.getLocalAddress(), principal, cmd, this.state, platform);
	}
    /**
     * Cancel any running actuation concerning <code>this</code>.
     * @param cmd
     */
    protected void cancelActuation(ActuationCancelCommand cmd){
        if(!this.priorityCoordinator.executionAllowedFor(cmd)) return;
        bulbsHwService.cancelActuation(this.id);
    }
    /**
     * Set <code>this</code>' state from <code>refBulb</code> that was derived from
     * corresponding hardware interface and store it.
     * @param refBulb Hardware state
     */
    protected void syncToHardwareState(Bulb refBulb){
        Assert.isTrue(this.sameIdentityAs(refBulb));
        setName(refBulb.getName());
        setState(refBulb.getState());
        getBulbAttributes().putAll(refBulb.getBulbAttributes());
        setOnline(refBulb.getOnline());
        if(log.isDebugEnabled()){
            log.debug("Bulb["+ this.id +"] state synched from HW: " + refBulb.getState());
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean sameIdentityAs(Bulb other) {
        if(other == null ) return false;
        if( !this.id.sameValueAs(other.id) )return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Bulb other = (Bulb) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
	/**
	 * 
	 * @param name
	 */
	private void setName(String name){
        this.name = name;
	}
    private void setPlatform(BulbsPlatform platform) {
        this.platform = platform;
    }
    private void setState(BulbState state) {
        this.state = state;
    }
    private void setOnline(Boolean online) {
        this.online = online;
    }
    private void setPriorityCoordinator(PriorityCoordinator priorityCoordinator) {
        this.priorityCoordinator = priorityCoordinator;
    }

    protected void setBridge(BulbBridge bridge) {
        this.bridge = bridge;
    }
    

}