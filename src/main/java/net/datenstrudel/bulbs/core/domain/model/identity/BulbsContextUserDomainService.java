package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface BulbsContextUserDomainService {

    /**
     * This method simplifies retrieval of a {@link BulbBridge}, {@link AppId} and
     * {@link BulbsContextUser} specific <code>BulbsPrincipal</code>. These principals
     * are stored for each user after successful initial linking process 
     *
     * @param userApiKey {@link BulbsContextUser} specific api key
     * @param appId Identity of the application requesting a resource/interaction
     * @param bridgeId The identifier of the {@link BulbBridge} for whose interaction
     * the result of this method is required
     * @return principal required for interactions with {@link BulbBridge} of identity <code>bridgeId</code>
     */
    public BulbsPrincipal loadPrincipalByUserApiKey(String userApiKey, AppId appId, BulbBridgeId bridgeId);
    /**
     * Auto generates a reasonable BulbsPrincipal object from parameters given.
     * Result won't be persisted, due to this method serves as a factory.
     * @param appId ID of the app that wants to be linked to core
     * @return 
     */
    public BulbsPrincipal bulbsPrincipalInstanceForNewBridge(AppId appId);

    public String createNewApiKey();
}