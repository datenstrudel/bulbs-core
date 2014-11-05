package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface BulbsContextUserService extends 
        UserDetailsService, 
        AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>{

    public BulbsContextUser loadById(BulbsContextUserId userId);
	/**
     * 
     * @param email
     * @param credentials
     * @param nickname
     * @param notificationHandler Validation error notification handler; must not be <code>null</code>
     * @return  
	 */
	public BulbsContextUser signUp(String email, String credentials, String nickname,
            NotificationHandlerBulbsContextUser notificationHandler);
	public BulbsContextUser signIn(String email, String credentials);
    public void modifyPassword(String apiKey, String newCredentials, NotificationHandlerBulbsContextUser validationNotificationHandler);
	/**
     * Finally add a <code>newPrincipal</code> to the user having the <code>userId</code> supplied.
     * This operation usually is exclusively invoked from a corresponding event handling
     * after the <code>newPrincipal</code> was already successfully created on a
     * {@link BulbBridge}'s underlying hardware device. Thus this service only assigns the 
     * <code>newPrincipal</code> to the <code>BulbsContextUser</code> within the core context.
     * @param userId
     * @param newPrincipal 
     */
    public void addBulbsPrincipal(BulbsContextUserId userId, BulbsPrincipal newPrincipal);
    /**
     * Finally remove <code>principal</code> from the user, having the 
     * <code>userId</code>.
     * @param userId
     * @param principal 
     */
    public void removeBulbsPrincipal(BulbsContextUserId userId, BulbsPrincipal principal);
}