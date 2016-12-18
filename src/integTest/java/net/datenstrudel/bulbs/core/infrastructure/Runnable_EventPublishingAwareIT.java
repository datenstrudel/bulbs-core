package net.datenstrudel.bulbs.core.infrastructure;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class Runnable_EventPublishingAwareIT extends AbstractBulbsIT {

    @Autowired
    @Qualifier("taskExecutor")
    private AsyncTaskExecutor asyncExecutor;

    private volatile boolean testListenToDomainEvents_withinAsyncContext_passed = false;

    @Test
    public void listenToDomainEvents_withinAsyncContext() throws Exception {
        System.out.println("listenToDomainEvents_withinAsyncContext");

        asyncExecutor.execute(new Runnable_EventPublishingAware() {
            @Override
            public void execute() {
                System.out.println("Async Runnable executed.");
                DomainEventPublisher publisher = DomainEventPublisher.instance();

                ThreadLocal<Map> expSubscr = (ThreadLocal<Map>) ReflectionTestUtils.getField(publisher, "subscribers");
                testListenToDomainEvents_withinAsyncContext_passed = !expSubscr.get().isEmpty();
            }
        });
        Thread.sleep(200l);
        assertTrue(testListenToDomainEvents_withinAsyncContext_passed);
    }

}