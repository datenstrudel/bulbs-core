package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;

import java.util.Set;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public interface PresetRepository {

    public PresetId nextIdentity(BulbsContextUserId userId);
    public Preset loadById(PresetId presetId);
    public Preset loadByName(BulbsContextUserId userId, String presetName);
    public Set<Preset> loadByOwner(BulbsContextUserId userId);
    
    public void store(Preset group);
    public void remove(PresetId presetId);
}