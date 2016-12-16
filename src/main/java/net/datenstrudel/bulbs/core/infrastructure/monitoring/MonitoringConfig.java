package net.datenstrudel.bulbs.core.infrastructure.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.infrastructure.monitoring"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class MonitoringConfig {

    @Autowired
    ApplicationContext ctx;

    @Bean
    public MetricRepository metricRepository() {
        return new InMemoryMetricRepository();
    }
    @Bean
    public PublicMetrics threadPoolMetrics() {
        return new PublicMetricsAggregator(
            new ThreadPoolMetrics(ctx),
            new MetricReaderPublicMetrics(metricRepository())
        );
    }

}
