package net.datenstrudel.bulbs.core.infrastructure;

import com.mongodb.*;
import net.datenstrudel.bulbs.core.infrastructure.persistence.MongoConverter;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
//@EnableSpringConfigured
//@EnableLoadTimeWeaving
//@EnableAspectJAutoProxy
@Import({ 
    SerializerConfig.class
})
@PropertySource("classpath:/META-INF/applicationContext/bulbs-core-config.properties")
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.infrastructure.persistence"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
@EnableMongoRepositories()
public class PersistenceConfig extends AbstractMongoConfiguration{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(PersistenceConfig.class);
    
    @Value("${mongo.dbname}")
    private String dbName;
    @Value("${mongo.host}")
    private String host;
    @Value("${mongo.port}")
    private Integer port;
    
    @Value("${mongo.connections-per-host}")
    private Integer connectionsPerHost;
    @Value("${mongo.threads-allowed-to-block-for-connection-multiplier}")
    private Integer threadsAllowedToBlockForConnectionMultiplier;
    @Value("${mongo.connect-timeout}")
    private Integer connectTimeout;
    @Value("${mongo.max-wait-time}")
    private Integer maxWaitTime;
    @Value("${mongo.auto-connect-retry}")
    private Boolean autoConnectRetry;
    @Value("${mongo.socket-timeout}")
    private Integer socketTimeout;
    
    //~ Converters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected String getDatabaseName() {
        return dbName;
    }
    
    @Autowired
    Environment env;
    @Autowired
    ApplicationContext appCtx;

    @Override
    public Mongo mongo() throws Exception {env.getProperty("dbname");
        if(connectionsPerHost == null) return null;
        MongoClientOptions opts = MongoClientOptions.builder()
                .connectionsPerHost(connectionsPerHost)
                .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
                .connectTimeout(connectTimeout)
                .maxWaitTime(maxWaitTime)
                .autoConnectRetry(autoConnectRetry)
                .socketKeepAlive(true)
                .socketTimeout(socketTimeout)
//                .writeConcern(WriteConcern.ACKNOWLEDGED).build();
                .writeConcern(WriteConcern.UNACKNOWLEDGED).build();
        MongoClient res = new MongoClient(new ServerAddress(host, port ), opts);
        return res;
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Override
    public CustomConversions customConversions() {
        final List converters = new ArrayList<>();
        converters.addAll(appCtx.getBeansWithAnnotation(MongoConverter.class).values());
        final CustomConversions res = new CustomConversions(converters);
        log.info("Found and applied persistence converters: " + converters.size());
        return res;
    }


}