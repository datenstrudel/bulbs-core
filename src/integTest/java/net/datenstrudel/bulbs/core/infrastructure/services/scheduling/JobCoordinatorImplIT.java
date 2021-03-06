package net.datenstrudel.bulbs.core.infrastructure.services.scheduling;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import org.junit.Test;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Thomas Wendzinski
 */
public class JobCoordinatorImplIT extends AbstractBulbsIT {
    
    private static final Logger log = LoggerFactory.getLogger(JobCoordinatorImplIT.class);

    @Autowired
    private JobCoordinatorImpl instance;
            
    @Test
    public void testSchedule() throws Exception{
        System.out.println("schedule");
        String jobId = "testSchedule_jobId";
        CronExpression cronTrigger = new CronExpression("1/1 * * ? * *");
//        ZoneId zoneId = ZoneId.of("UTC");
        final ConcurrentLinkedDeque<Integer> invocations = new ConcurrentLinkedDeque<>();
        ScheduledJobExecutor jobExec = new ScheduledJobExecutor(){
            @Override
            public void execute(Map<String, Object> parameters) {
                log.info("Test Task executed");
                invocations.add(1);
            }
        };
        try{
            instance.schedule(jobId, cronTrigger, jobExec);
            Thread.sleep(2000l);
            assertTrue( invocations.size() > 0 );
        }finally{
            instance.unSchedule(jobId);
        }
    }
    @Test
    public void testUnSchedule() throws Exception{
        System.out.println("unSchedule");
        String jobId = "testSchedule_jobId";
        CronExpression cronTrigger = new CronExpression("1/1 * * ? * *");
        ZoneId zoneId = ZoneId.of("UTC");
        final ConcurrentLinkedDeque<Integer> invocations = new ConcurrentLinkedDeque<>();
        ScheduledJobExecutor jobExec = new ScheduledJobExecutor(){
            @Override
            public void execute(Map<String, Object> parameters) {
                log.info("Test Task executed");
                invocations.add(1);
            }
        };
        
        try{
            instance.schedule(jobId, cronTrigger, jobExec);
            Thread.sleep(2000l);
            assertTrue( invocations.size() > 0 );
            assertTrue( instance.isScheduled(jobId));
        
            instance.unSchedule(jobId);
            int invocationsOnStop = invocations.size();
            Thread.sleep(2000l);
            assertEquals(invocationsOnStop, invocations.size());
            assertTrue( !instance.isScheduled(jobId));
        }finally{
            instance.unSchedule(jobId);
        }
    }
    @Test
    public void testIsScheduled() throws Exception{
        System.out.println("isScheduled");
        String jobId = "testSchedule_jobId";
        CronExpression cronExpression = new CronExpression("1/1 * * ? * *");
        ZoneId zoneId = ZoneId.of("UTC");
        final ConcurrentLinkedDeque<Integer> invocations = new ConcurrentLinkedDeque<>();
        ScheduledJobExecutor jobExec =
                parameters -> log.info("Test Task executed");
        try{
            assertFalse(instance.isScheduled(jobId));
            instance.schedule(jobId, cronExpression, jobExec);
            assertTrue(instance.isScheduled(jobId));
        }finally{
            instance.unSchedule(jobId);
        }
    }
    
}
