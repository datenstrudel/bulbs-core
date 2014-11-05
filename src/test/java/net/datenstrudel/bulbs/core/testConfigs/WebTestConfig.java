package net.datenstrudel.bulbs.core.testConfigs;

import net.datenstrudel.bulbs.core.web.config.JsonHttpMessageConverter;
import net.datenstrudel.bulbs.core.web.controller.util.ControllerExceptionHandler;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
//    "net.datenstrudel.bulbs.core.web.config"
        
})
public class WebTestConfig extends WebMvcConfigurerAdapter{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(WebTestConfig.class);
    @Autowired
    JsonHttpMessageConverter gsonHttpMessageConverter;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
    
    @Bean ControllerExceptionHandler exceptionHandler(){
        return new ControllerExceptionHandler();
    }
    
    @Bean JsonHttpMessageConverter jsonConverter(){
        return new JsonHttpMessageConverter();
    }
    
    @Bean
    public LocalValidatorFactoryBean validator(){
        LocalValidatorFactoryBean res = new LocalValidatorFactoryBean();
        return res;
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> el : converters) {
            log.info("MSG CONVERTER FOUND: " + el.getClass().getSimpleName());
        }
        converters.add(gsonHttpMessageConverter);
        super.configureMessageConverters(converters); 
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
