package net.datenstrudel.bulbs.core.testConfigs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class InfrastructureServicesTestConfig {

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}