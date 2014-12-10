package net.datenstrudel.bulbs.core.infrastructure.monitoring;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.endpoint.VanillaPublicMetrics;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.infrastructure.monitoring"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class MonitoringConfig {

    @Inject
    ApplicationContext ctx;

    @Bean
    public MetricRepository metricRepository() {
        return new InMemoryMetricRepository();
    }
    @Bean
    public PublicMetrics threadPoolMetrics() {
        return new PublicMetricsAggregator(
            new ThreadPoolMetrics(ctx),
            new VanillaPublicMetrics(metricRepository())
        );
    }

}
