package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * A user class that can be used for context specific authentication/
 * authorization. Serves as wrapper for {@link BulbsPrincipal} for hardware side
 * authentication.
 * @author Thomas Wendzinski
 * @version 1.0
 */
public class BulbsContextUser extends Entity<BulbsContextUser, BulbsContextUserId>
        implements  UserDetails{

    //~ Member(s) //////////////////////////////////////////////////////////////
	/**
	 * Unique Identifier
	 */
	private String email;
	/**
	 * Saved as SHA Hash
	 */
	private String credentials;
	private String nickname;
    
    private String apiKey;
    
    private Set<BulbsPrincipal> bulbsPrincipals = new HashSet<>();

    //~ Construction ///////////////////////////////////////////////////////////
	private BulbsContextUser(){
	}
    public BulbsContextUser(
            BulbsContextUserId userId, 
            String email, 
            String credentials, 
            String nickname,
            String apiKey) {
        setId(userId);
        setEmail(email);
        setCredentials(credentials);
        setNickname(nickname);
        setApiKey(apiKey);
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public BulbsContextUserId getBulbsContextUserlId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getCredentials() {
        return credentials;
    }
    public String getNickname() {
        return nickname;
    }
    public String getApiKey() {
        return apiKey;
    }
    
    public Set<BulbsPrincipal> getBulbsPrincipals() {
        return bulbsPrincipals;
    }
    
    public void addBulbsPrincipal(BulbsPrincipal principal2Add){
        if(this.bulbsPrincipals.contains(principal2Add) )
            throw new IllegalArgumentException("Principal already exists: " + principal2Add);
        if(StringUtils.isEmpty(principal2Add.getBulbBridgeId() ))
            throw new IllegalArgumentException("Principals did not contain bulbBridgeId: " + principal2Add);
        
        principal2Add.setState(BulbsPrincipalState.REGISTERED);
        this.bulbsPrincipals.add(principal2Add);
    }
    public void removeBulbsPrincipal(BulbsPrincipal principal2Remove){
        this.bulbsPrincipals.remove(principal2Remove);
    }
    public BulbsPrincipal principalFrom(BulbBridgeId bridgeId){
        for (BulbsPrincipal el : bulbsPrincipals) {
            if(el.getBulbBridgeId().equals(bridgeId.getUuId()))
                return el;
        }
        throw new IllegalArgumentException("No Principal for bridge["+bridgeId+"] exists!");
    }
    public void removeBulbsPrincipalsByBridge(BulbBridgeId bridgeId){
        Set<BulbsPrincipal> p2Del = new HashSet<>();
        for (BulbsPrincipal p : bulbsPrincipals) {
            if(p.getBulbBridgeId().equals(bridgeId.getUuId())) p2Del.add(p);
        }
        bulbsPrincipals.removeAll(p2Del);
    }

    public void modifyPassword(String newPassword){
        this.setCredentials(newPassword);
    }

    public void validateNew(ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser notificationHandler,
            BulbsContextUserRepository userRepository){
        new ValidatorBulbsContextUser(this, userRepository, null, notificationHandler)
                .validateNew();
    }
    public void validateExisting(ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser notificationHandler,
            BulbsContextUserRepository userRepository, String oldMailAddress){
        new ValidatorBulbsContextUser(this, userRepository, oldMailAddress, notificationHandler)
                .validateExisting();
    }

    
    @Override
    public boolean sameIdentityAs(BulbsContextUser other) {
        if(other == null)return false;
        if(!this.id.sameValueAs(id))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final BulbsContextUser other = (BulbsContextUser) obj;
        return this.sameIdentityAs(other);
    }
    @Override
    public String toString() {
        return "BulbsContextUser{" 
                + "id=" + id
                + ", email=" + email 
                + ", credentials= [PROTECTED]" 
                + ", nickname=" + nickname 
                + ", bulbsPrincipals=" + bulbsPrincipals + '}';
    }
    
    //~ Implemented for UserDetails ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new LinkedList<>();
    }
    @Override
    public String getPassword() {
        return this.credentials;
    }
    @Override
    public String getUsername() {
        return this.email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setEmail(String email) {
        if(email == null || email.isEmpty())throw new IllegalArgumentException("Empty field 'email' not allowed.");
        this.email = email;
    }
    private void setCredentials(String credentials) {
        if(credentials == null)throw new IllegalArgumentException("Empty field 'credentials' not allowed.");
        this.credentials = credentials;
    }
    private void setNickname(String nickname) {
        if(nickname == null || nickname.isEmpty())throw new IllegalArgumentException("Empty field 'nickname' not allowed.");
        this.nickname = nickname;
    }
    private void setBulbsPrincipals(Set<BulbsPrincipal> bulbsPrincipals) {
        this.bulbsPrincipals = bulbsPrincipals;
    }
    private void setApiKey(String apiKey) {
//        if(apiKey == null || apiKey.isEmpty())throw new IllegalArgumentException("Empty field 'apiKey' not allowed.");
        this.apiKey = apiKey;
    }

}