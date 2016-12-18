package net.datenstrudel.bulbs.core.config;

import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.core.infrastructure.SerializerConfig;
import net.datenstrudel.bulbs.core.infrastructure.monitoring.MonitoringConfig;
import net.datenstrudel.bulbs.core.infrastructure.services.InfrastructureServicesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@EnableScheduling
@Import({
    PersistenceConfig.class,
    SerializerConfig.class,
    InfrastructureServicesConfig.class,
    MonitoringConfig.class,
})
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.domain"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class BulbsCoreConfig {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsCoreConfig.class);

    @Autowired
    private Environment env;

    @Value("${info.build.artifact:[unknown]}")
    String artifact;
    @Value("${info.build.name:[unknown]}")
    String artifactName;
    @Value("${info.build.version:[unknown]}")
    String version;


    //~ Construction ///////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        String msg = "Started BulbsCoreConfig using profile(s):";
        for (String el : env.getActiveProfiles()) {
            msg += " [" + el + "]";
        }
        log.info(msg);
        log.info("+++ Artifact: " + artifact);
        log.info("+++ Name:     " + artifactName);
        log.info("+++ Version:  " + version);

    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Bean
    public AsyncTaskExecutor taskExecutor(
            @Value("${taskExecutor.corePoolsize}") int corePoolsize,
            @Value("${taskExecutor.maxPoolsize}") int maxPoolsize,
            @Value("${taskExecutor.queueCapacity}") int queueCapacity,
            @Value("${taskExecutor.keepAliveSeconds}") int keepAliveSeconds
    ){
        ThreadPoolTaskExecutor res = new ThreadPoolTaskExecutor();
        res.setCorePoolSize(corePoolsize);
        res.setMaxPoolSize(maxPoolsize);
        res.setQueueCapacity(queueCapacity);
        res.setKeepAliveSeconds(keepAliveSeconds);
        res.setThreadNamePrefix("exec-bulbs-core-");
        return res;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
