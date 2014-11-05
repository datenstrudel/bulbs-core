package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.AppLinkResult;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:41
 */
public interface BulbBridgeDomainService {
    
    /**
     * Allows 3rd party applications to link to this core. <br /><br />
     * Internally {@link BulbsPrincipal} instances are created for each bridge that can
     * be found for the concerning user account. Such a principal is registered with the
     * hardware if supported. This process is idempotent in the way that existing principals
     * won't be overriden if already existing for <code>appId</code>.
     * 
     * @param userApiKey 
     * @param appId of app requesting
     * @return Result containing states and <code>apiKey</code>.
     * @throws IllegalArgumentException in case an authentication error occurrs. 
     * @deprecated 
     */
    @Deprecated
    public AppLinkResult linkAppToCore(String userApiKey, AppId appId)throws IllegalArgumentException;
	/**
	 * Initial Factory method. Checks whether the bridge, identified by its MAC
	 * address already exists. If so, an exception is thrown.
	 * 
      * @param localAddress
      * @param platform 
      * @param user Future owner of the new bridge
      * @return
      * @throws BulbBridgeHwException  
	 */
	public BulbBridge findAndCreateBulbBridge(
            BulbBridgeAddress localAddress, 
            BulbsPlatform platform, 
            BulbsContextUser user
            )throws BulbBridgeHwException;
    
    public void removeBulbBridge(
            BulbBridgeId bridgeId, 
            BulbsContextUser initiator);

    /**
     * Actually remove given <code>principals2Remove</code> from underlying hardware resource
     * @param principal
     * @param principals2Remove
     * @param bridgeAddress
     * @param platform
     * @param user
     */
    public void removeBulbsPrincipalsAfterDeletion(
            BulbsPrincipal principal,
            BulbsPrincipal[] principals2Remove,
            BulbBridgeAddress bridgeAddress,
            BulbsPlatform platform,
            BulbsContextUser user ) throws BulbBridgeHwException;

}