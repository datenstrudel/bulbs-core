package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Set;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:41
 */
public interface BulbBridgeRepository {

    public BulbBridgeId nextIdentity();
	/**
	 * 
     * @param id
     * @return  
	 */
	public BulbBridge loadById(BulbBridgeId id);
	/**
	 * 
     * @param userId
     * @return  
	 */
	public Set<BulbBridge> loadByOwner(BulbsContextUserId userId);

	/**
	 * Save the current state of a BulbBridge
	 * 
	 * @param bulbBridge
	 */
	public void store(BulbBridge bulbBridge);
	/**
	 * 
	 * @param id
	 */
	public void remove(BulbBridgeId id);

}