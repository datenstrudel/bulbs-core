package net.datenstrudel.bulbs.core.web.config;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
/**
 *
 * @author Thomas Wendzinski
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.web"
        
})
@PropertySource("classpath:/bulbs-core-config.properties")
public class WebConfig extends WebMvcConfigurerAdapter{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);


    @Autowired
    JsonHttpMessageConverter gsonHttpMessageConverter;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(36000000);
        registry.addResourceHandler("/bulbsCore/**")
                .addResourceLocations("classpath:/static/bulbsCore/**")
                .setCachePeriod(36000000);
        registry.addResourceHandler("/bulbsCore/js/**")
                .addResourceLocations("classpath:/static/bulbsCore/js/**")
                .setCachePeriod(36000000);
        registry.addResourceHandler("/errorpages/**")
                .addResourceLocations("classpath:/static/errorpages/**")
                .setCachePeriod(36000000);
        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/static/swagger/**")
                .setCachePeriod(36000000);
    }
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Configure message converters..");
        for (HttpMessageConverter<?> el : converters) {
            log.info("MSG CONVERTER FOUND: " + el.getClass().getSimpleName());
        }
        converters.add(gsonHttpMessageConverter);
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        converters.add(jacksonConverter);
        super.configureMessageConverters(converters); 
    }
    /**
     * Enable JSR-303 Bean validation
     * @return validator factory
     */
    @Bean
    public LocalValidatorFactoryBean validator(){
        return new LocalValidatorFactoryBean();
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
