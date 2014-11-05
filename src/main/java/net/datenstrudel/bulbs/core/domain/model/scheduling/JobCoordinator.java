package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.infrastructure.services.scheduling.ScheduledJobExecutor;
import org.quartz.CronExpression;

/**
 * Describes a simple, internal, infrastructure abstracting, 
 * domain concern independent scheduler facade.
 * 
 * @author Thomas Wendzinski
 */
public interface JobCoordinator {

    /**
     * Schedule a new job. A job, that is identified by its <code>jobId</code>,
     * that is already existing is not overriden on further calls.
     * @param jobId Who.
     * @param cronTrigger When.
     * @param jobExec What.
     */
    void schedule(String jobId, CronExpression cronTrigger, ScheduledJobExecutor jobExec);
    /**
     * Unschedule a job. Keeps calm in case jobId doesn't exist.
     * @param jobId 
     */
    void unSchedule(String jobId);
    boolean isScheduled(String jobId);
    
}
