package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:41
 */
public interface BulbBridgeRepository
		extends MongoRepository<BulbBridge, BulbBridgeId>, BulbBridgeRepositoryExt {

	public Set<BulbBridge> findByOwner(BulbsContextUserId owner);
	public Set<BulbBridge> findByOwnerAndBulbs_Name(BulbsContextUserId owner, String bulbName);

}