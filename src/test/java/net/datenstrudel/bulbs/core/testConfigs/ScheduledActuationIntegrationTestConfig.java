package net.datenstrudel.bulbs.core.testConfigs;

import net.datenstrudel.bulbs.core.application.services.ScheduledActuationService;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisherDeferrer;
import org.easymock.EasyMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@ComponentScan(
        basePackages = {"net.datenstrudel.bulbs.core.domain.model.infrastructure"},
        excludeFilters = @ComponentScan.Filter(Configuration.class))
//@Import(value = {SchedulerInfrastructureConfig.class})
public class ScheduledActuationIntegrationTestConfig {

    @Bean
    public ScheduledActuationService scheduledActuationService_Mk() {
        return EasyMock.createStrictMock(ScheduledActuationService.class);
    }

    @Bean
    public DomainServiceLocator domainServiceLocator_Mk() {
        return EasyMock.createNiceMock(DomainServiceLocator.class);
    }

    @Bean
    public DomainEventPublisherDeferrer domainEventPublisherDeferrer_Mk() {
        return EasyMock.createMock(DomainEventPublisherDeferrer.class);
    }

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
