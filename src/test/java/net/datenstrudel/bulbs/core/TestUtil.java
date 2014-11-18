package net.datenstrudel.bulbs.core;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

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
}
