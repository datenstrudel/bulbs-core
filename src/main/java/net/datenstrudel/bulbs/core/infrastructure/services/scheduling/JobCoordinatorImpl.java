package net.datenstrudel.bulbs.core.infrastructure.services.scheduling;

import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class JobCoordinatorImpl implements JobCoordinator {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(JobCoordinatorImpl.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactory;
    private Scheduler scheduler;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public JobCoordinatorImpl() {
    }
    
    @PostConstruct
    public void init(){
        scheduler = schedulerFactory.getObject();
    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void schedule(String jobId, CronExpression cronTrigger, final ScheduledJobExecutor jobExec){
        
        final String triggerId = "jobTrigger-" + jobId;
        if(isScheduled(triggerId)){
            log.info("Scheduled Job["+jobId+"] not executed, due to already scheduled.");
            return;
        }
        JobDataMap jobData = new JobDataMap();
        jobData.put("jobExecutor", jobExec);
        final JobDetail jobDetail = JobBuilder.newJob( JobAdapter.class )
                .storeDurably(false)
                .withIdentity(jobId)
                .setJobData(jobData)
                .build();
        final Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerId)
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(cronTrigger)
                        .inTimeZone(cronTrigger.getTimeZone()) 
                ).build();
        
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        
    }
    @Override
    public void unSchedule(String jobId){
        if(!isScheduled(jobId))return ;
        try {
            scheduler.deleteJob(JobKey.jobKey(jobId));
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    @Override
    public boolean isScheduled(String jobId){
        try {
            return scheduler.checkExists(JobKey.jobKey(jobId));
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    public static class JobAdapter implements Job{
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            ScheduledJobExecutor exec = (ScheduledJobExecutor) context.getMergedJobDataMap().get("jobExecutor");
            exec.execute(context.getMergedJobDataMap() );
        }
    }
}
