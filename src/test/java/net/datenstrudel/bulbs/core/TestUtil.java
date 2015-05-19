package net.datenstrudel.bulbs.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Thomas Wendzinski.
 */
public class TestUtil {

    public static Object unwrappedProxiedBean(Object bean) throws Exception{
        if(AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Object target = ((Advised) bean).getTargetSource().getTarget();
            return target;
        }
        throw new IllegalArgumentException("The object supplied is no AOP proxy: " + bean);
    }

    public static <T> T jacksonDeserialize(Class<T> clazz, String json) throws IOException {
        return new ObjectMapper().reader(clazz).readValue(json);
    }
    public static <T> List<T> jacksonDeserializeList(Class<T> clazz, String json) throws IOException {
        ObjectMapper mapper = new WebConfig().getMappingJackson2HttpMessageConverter().getObjectMapper();
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
    public static String jacksonSerialize(Object toSerialize) throws IOException {
        ObjectMapper mapper = new WebConfig().getMappingJackson2HttpMessageConverter().getObjectMapper();
        return mapper.writeValueAsString(toSerialize);
    }
}
