package net.datenstrudel.bulbs.core.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.TimeZone;

/**
 * @author Thomas Wendzinski
 */
@Configuration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.web"
}, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper res = new ObjectMapper();
        res.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        res.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        res.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        res.setTimeZone(TimeZone.getTimeZone("UTC"));
        res.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        res.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return res;
    }
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(jacksonObjectMapper());
        return jacksonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Configure message converters..");
        converters.add(getMappingJackson2HttpMessageConverter());
        for (HttpMessageConverter<?> el : converters) {
            log.info("MSG CONVERTER registered: " + el.getClass().getSimpleName());
        }
        super.configureMessageConverters(converters);
    }
}
