package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

import java.io.Serializable;

/**
 * A principal that is used to authenticate against the BulbBridge. This is necessary
 * in order to be allowed to execute commands on hardware.
 * 
 * @author Thomas Wendzinski
 * @version 1.0
 * @updated 08-Jun-2013 22:55:00
 */
public class BulbsPrincipal implements ValueObject<BulbsPrincipal>, Serializable{

    //~ Member(s) //////////////////////////////////////////////////////////////
	private String username;
	private AppId appId;
	private String bulbBridgeId;
    /**
     * Firstly, a principal is persisted in the local bulbs core and thus 
     * <code>PENDING</code>. Eventually it is requested to be added to the underlying
     * hardware BulbBridge. Successful addition to the bridge will trigger an Event with
     * principal beeing in <code>COMPLETED</code> state.
     */
	private BulbsPrincipalState state;

    //~ Construction ///////////////////////////////////////////////////////////
	private BulbsPrincipal(){}
    public BulbsPrincipal(
            String username, 
            AppId appId, 
            String bulbBridgeId, 
            BulbsPrincipalState state) {
        setUsername(username);
        setAppId(appId);
        setBulbBridgeId(bulbBridgeId);
        setState(state);
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getUsername() {
        return username;
    }
    public AppId getAppId() {
        return appId;
    }
    public String getBulbBridgeId() {
        return bulbBridgeId;
    }
    public BulbsPrincipalState getState() {
        return state;
    }
    
    public void setState(BulbsPrincipalState state) {
        this.state = state;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 97 * hash + (this.appId != null ? this.appId.hashCode() : 0);
        hash = 97 * hash + (this.bulbBridgeId != null ? this.bulbBridgeId.hashCode() : 0);
        hash = 97 * hash + (this.state != null ? this.state.hashCode() : 0);
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
        final BulbsPrincipal other = (BulbsPrincipal) obj;
        return this.sameValueAs(other);
    }
    @Override
    public boolean sameValueAs(BulbsPrincipal other) {
        if(other == null)return false;
        if(!this.username.equals(other.username))return false;
        if(!this.appId.equals(other.appId))return false;
        if(!this.bulbBridgeId.equals(other.bulbBridgeId))return false;
        if(!this.state.equals(other.state))return false;
        return true;
    }
    @Override
    public String toString() {
        return "BulbsPrincipal{" 
                + "username=" + username 
                + ", appType=" + appId 
                + ", bulbBridgeId=" + bulbBridgeId 
                + ", state=" + state + '}';
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setUsername(String username) {
        if(username == null) throw new IllegalArgumentException("Username must not be null!");
        if(username.length() > 40 || username.length() < 2)
            throw new IllegalArgumentException("Username must be of length betweeen 2 and 40");
        this.username = username;
    }
    private void setAppId(AppId appType) {
        if(appType == null) throw new IllegalArgumentException("AppType must not be null!");
        if(appType.getUniqueAppName().length() > 40 || appType.getUniqueAppName().length() < 5)
            throw new IllegalArgumentException("AppType must be of length betweeen 5 and 40");
        this.appId = appType;
    }
    private void setBulbBridgeId(String bulbBridgeId) {
        this.bulbBridgeId = bulbBridgeId;
    }
    
    

}