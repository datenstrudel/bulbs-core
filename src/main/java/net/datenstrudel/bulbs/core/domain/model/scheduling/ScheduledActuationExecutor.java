package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.application.services.ScheduledActuationService;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.core.infrastructure.services.scheduling.ScheduledJobExecutor;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * 
 * @author Thomas Wendzinski
 */
public class ScheduledActuationExecutor implements ScheduledJobExecutor{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationExecutor.class);
    private final ScheduledActuationId entityId;
    private final BulbsContextUserId invoker;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public ScheduledActuationExecutor(
            ScheduledActuationId entityId, 
            BulbsContextUserId invoker, 
            String uniqueName) {
        this.entityId = entityId;
        this.invoker = invoker;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void execute(Map<String, Object> parameters) {
        final ScheduledActuationService actuatorService = 
                DomainServiceLocator.getBean(ScheduledActuationService.class);
        log.info("Going to execute scheduled actuation: " + entityId);
        actuatorService.execute(invoker, entityId);
        DomainEventPublisher.instance().publish(new ScheduledActuationExecuted(entityId));
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
