package net.datenstrudel.bulbs.core.infrastructure.services.scheduling;

import java.util.Map;

/**
 * Wraps the logic that is invoked when a scheduler triggers a job. Implementation thus
 * depends on domain context.
 * 
 * @author Thomas Wendzinski
 */
@FunctionalInterface
public interface ScheduledJobExecutor {
    
    /**
     * Execute the domain job triggered.
     * @param parameters provided by scheduler context
     */
    public void execute(Map<String, Object> parameters);
    
}
