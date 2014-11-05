package net.datenstrudel.bulbs.core.domain.model.identity;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface BulbsContextUserRepository {

    public BulbsContextUserId nextIdentity();
    
	public BulbsContextUser loadByEmail(String email);
	/**
	 * 
     * @param id
     * @return  
	 */
	public BulbsContextUser loadById(BulbsContextUserId id);
	public BulbsContextUser loadByApiKey(String apiKey);
	/**
	 * 
	 * @param user
	 */
	public void remove(BulbsContextUser user);
	/**
	 * 
	 * @param user
	 */
	public void store(BulbsContextUser user);

}