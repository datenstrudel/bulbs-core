package net.datenstrudel.bulbs.core.testConfigs;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.web.controller.util.ControllerExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Correesponds to {@link net.datenstrudel.bulbs.core.web.config.WebConfig} but doesn't load all
 * controllers for easier test setup regarding mocks
 */
@Configuration
@EnableWebMvc
@WebAppConfiguration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.web.config"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class WebTestConfig extends WebMvcConfigurerAdapter {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(WebTestConfig.class);

    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    ControllerExceptionHandler exceptionHandler() {
        return new ControllerExceptionHandler();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean res = new LocalValidatorFactoryBean();
        return res;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new WebConfig().getMappingJackson2HttpMessageConverter();
    }
    @Bean
    public ObjectMapper jacksonObjectMapper() {
        return new WebConfig().jacksonObjectMapper();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Configure message converters..");
        converters.add(mappingJackson2HttpMessageConverter());
        for (HttpMessageConverter<?> el : converters) {
            log.info("MSG CONVERTER registered: " + el.getClass().getSimpleName());
        }
        super.configureMessageConverters(converters);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
