package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.AppLinkResult;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

import java.util.Set;

/**
 * Note that any operation that does not require an {@link AppId} is supposed to 
 * be invoked as bulbsCore operation only.
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:41
 */
public interface BulbBridgeAdminService {

    /**
     * Make sure that the app, having the <code>appId</code> given, has access to all bridges
     * and their bulbs. The link process creates app specific {@link BulbsPrincipal}s for each
     * bridge registered for the user (associated by its <code>userApiKey</code>) if necessary.
     * Usually the link button should be pressed in order to write principals to bridge(s).
     * 
     * @param userApiKey 
     * @param appId
     * @return 
     * @deprecated 
     */
    public AppLinkResult linkAppToCore(
            String userApiKey, 
            AppId appId);
    
	/**
      * Allows initial Bulb Bridge creation. Underlying domain service checks whether the 
      * bridge, identified by its mac address already exists. If so, an exception is thrown. <br />
      * Usually link button must be pressed.
      * 
      * @param platform
      * @param localAddress
      * @param userApiKey 
      * @return
      * @throws BulbBridgeHwException  
	 */
	public BulbBridge findAndCreateBulbBridge(
            BulbsPlatform platform, 
            BulbBridgeAddress localAddress, 
            String userApiKey)throws BulbBridgeHwException;
    public void performBulbSearch(
            BulbBridgeId bridgeId, String userApiKey) throws BulbBridgeHwException;
    /**
     * Syncs all bridges (the user has access to).
     * @param userApiKey
     * @throws BulbBridgeHwException 
     */
    public void syncAllBridges(String userApiKey) throws BulbBridgeHwException;

    public BulbBridge loadBridge(BulbBridgeId bridgeId, String userApiKey);
    public Set<BulbBridge> bridgesByContextUser(String userApiKey);

    public Bulb loadBulb(BulbId bulbId, String userApiKey);
    public Set<Bulb> bulbsByContextUser(String userApiKey);
    public String[] allBulbNames(String userApiKey);
    
    public void modifyBridgeName(
            String userApiKey, 
            BulbBridgeId bridgeId, 
            String newName)throws BulbBridgeHwException;
    
	/**
	 * 
     * @param userApiKey 
	 * @param bulbId
     * @param newName
     * @throws BulbBridgeHwException  
	 */
	public void modifyBulbName(
            String userApiKey,
            BulbId bulbId,
            String newName)throws BulbBridgeHwException;

	/**
	 * 
     * @param userApiKey 
     * @param bridgeId
     * @param newAddress
     * @throws BulbBridgeHwException  
	 */
	public void modifyLocalAddress(
            String userApiKey,
            BulbBridgeId bridgeId, 
            BulbBridgeAddress newAddress)throws BulbBridgeHwException;

	
	/**
     * 
     * @param userApiKey 
     * @param bulbBridgeId
     * @param principal2Remove
     * @throws BulbBridgeHwException  
	 */
	public void removeBulbsPrincipal(
            String userApiKey,
            BulbBridgeId bulbBridgeId, 
            BulbsPrincipal principal2Remove) throws BulbBridgeHwException;
    
    /**
     * Remove a {@link BulbBridge} given by its <code>bridgeId</code>. Additionally all
     * associated entities will be removed.
     * (Event beeing propagated that shall trigger deletion of concerning objects involved.)
     * 
     * @param userApiKey 
     * @param bridgeId 
     */
    public void removeBulbBridge(
            String userApiKey,
            BulbBridgeId bridgeId );

}