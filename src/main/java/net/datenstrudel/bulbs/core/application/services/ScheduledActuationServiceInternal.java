package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;

/**
 * For internal, message driven use cases only
 */
public interface ScheduledActuationServiceInternal {

    public void deleteAfterExecutionIfConfigured(ScheduledActuationId id);
}
