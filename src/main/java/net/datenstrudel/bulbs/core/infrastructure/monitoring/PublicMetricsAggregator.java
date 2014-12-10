package net.datenstrudel.bulbs.core.infrastructure.monitoring;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Wendzinski.
 */
public class PublicMetricsAggregator implements PublicMetrics {

    private final PublicMetrics[] metricses;

    public PublicMetricsAggregator(PublicMetrics... metrics) {
        this.metricses = metrics;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        List<Metric<?>> res = Arrays.stream(this.metricses)
                .map(m -> m.metrics())
                .flatMap( (in) -> in.stream() )
                .collect(Collectors.toList());
        return res;
    }
}
