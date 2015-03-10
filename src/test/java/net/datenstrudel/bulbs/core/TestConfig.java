package net.datenstrudel.bulbs.core;

import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsHwService;
import org.easymock.EasyMock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
public class TestConfig
        implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);
    private static final String TEST_PROPERTIES_PATH = 
            "classpath:/bulbs-core-config-test.properties";
    
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Bean(name = "bulbsHwService")
    public BulbsHwService bulbsHwService_mk(){
        BulbsHwService res = EasyMock.createStrictMock(BulbsHwService.class);
        return res;
    }
    @Bean
    public CounterService counterService_mk() {
        CounterService res = Mockito.mock(CounterService.class);
        return res;
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        log.info("INIT TestConfig");
        //~ Load (test overriding) property resource(s) to be registered in environment with highest precedence!
        ConfigurableEnvironment env = ctx.getEnvironment();
        
        Resource runtimeProps = ctx.getResource(
                TEST_PROPERTIES_PATH);
        try{
            env.getPropertySources().addFirst(new ResourcePropertySource(runtimeProps));
        }catch(IOException ioex){
            throw new Error(ioex);
        }
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
