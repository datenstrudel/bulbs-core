package net.datenstrudel.bulbs.core.infrastructure.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.infrastructure.services.scheduling",
        "net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter"
}, excludeFilters = @ComponentScan.Filter(Configuration.class) )
public class InfrastructureServicesConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactory(){
        SchedulerFactoryBean res = new SchedulerFactoryBean();
        res.setWaitForJobsToCompleteOnShutdown(false);
        return res;
    }

}
