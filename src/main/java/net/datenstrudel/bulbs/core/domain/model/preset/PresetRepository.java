package net.datenstrudel.bulbs.core.domain.model.preset;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.BCoreBaseRepository;

import java.util.Set;

/**
 */
public interface PresetRepository extends BCoreBaseRepository<Preset, PresetId>, PresetRepositoryExt{

    public Preset findByNameAndId_Creator(String name, BulbsContextUserId creator);
    public Set<Preset> findById_Creator(BulbsContextUserId userId);
    
}