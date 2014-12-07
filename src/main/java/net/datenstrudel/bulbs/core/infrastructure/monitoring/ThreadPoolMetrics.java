package net.datenstrudel.bulbs.core.infrastructure.monitoring;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Thomas Wendzinski.
 */
public class ThreadPoolMetrics implements PublicMetrics {

    private final ApplicationContext ctx;

    public ThreadPoolMetrics(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        Collection<ThreadPoolTaskExecutor> taskExecutors =
                ctx.getBeansOfType(ThreadPoolTaskExecutor.class).values();
        final Collection<Metric<?>> res = new ArrayList<>();
        res.add(new Metric("taskExecutors.count", taskExecutors.size()));

        taskExecutors.stream().forEach((executor) -> {
            String execName = formatExecName(executor.getThreadNamePrefix());
            res.add(new Metric("taskExecutors." + execName + ".active", executor.getActiveCount()));
            res.add(new Metric("taskExecutors." + execName + ".poolSize", executor.getPoolSize()));
            res.add(new Metric("taskExecutors." + execName + ".corePoolSize", executor.getCorePoolSize()));
            res.add(new Metric("taskExecutors." + execName + ".keepAliveSeconds", executor.getKeepAliveSeconds()) );
            res.add(new Metric("taskExecutors." + execName + ".queue.size", executor.getThreadPoolExecutor().getQueue().size()) );
            res.add(new Metric("taskExecutors." + execName + ".queue.remainingCapacity", executor.getThreadPoolExecutor().getQueue().remainingCapacity()) );
        });
        return res;
    }
    private String formatExecName(String in) {
        return in.substring(0, in.lastIndexOf("-"));
    }
/*    @Override
    public Collection<Metric<?>> metrics() {
        Collection<ThreadPoolTaskExecutor> taskExecutors =
                ctx.getBeansOfType(ThreadPoolTaskExecutor.class).values();
        final Collection<Metric<?>> res = new ArrayList<>();
        res.add(new Metric("taskExecutors.count", taskExecutors.size()));

        taskExecutors.stream().forEach((executor) -> {
            res.add(new Metric("taskExecutors." + executor.getThreadNamePrefix() + ".active", executor.getActiveCount()));
            res.add(new Metric("taskExecutors." + executor.getThreadNamePrefix() + ".poolSize", executor.getPoolSize()));
        });
        return res;
    }*/
}
