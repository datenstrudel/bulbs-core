package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;

/**
 * Describes application service capabilities regarding {@link BulbBridge}s for core internal
 * use cases only (such as reacting to domain events). 
 * @author Thomas Wendzinski
 */
public interface IBulbBridgeAdminServiceInternal {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * Update the internal state of a {@link Bulb}. Supposed to be invoked _after_ 
     * the state was applied to the actual hardware. This method makes sure that the
     * state that was applied to the hardware is synchronized with the model that represents
     * the hardware.
     * @param bulbId
     * @param state 
     */
    public void updateBulbStateInternal(BulbId bulbId, BulbState state);
    public void syncToHardwareStateInternal(BulbBridgeId bridgeId, BulbsPrincipal principal);

    /**
     * Make sure that all {@link net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal}s
     * ,belonging to the bridge with its given <code>bridgeId</code>, are deleted from the
     * actual underlying hardware.
     * @param userId
     * @param bridgeId
     * @param address
     * @param platform
     */
    public void removeAllBulbsPrincipalsAfterBridgeDeletion(
            final BulbsContextUserId userId, final BulbBridgeId bridgeId, BulbBridgeAddress address, BulbsPlatform platform);

}
