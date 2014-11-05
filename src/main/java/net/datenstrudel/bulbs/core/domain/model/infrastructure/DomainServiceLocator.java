package net.datenstrudel.bulbs.core.domain.model.infrastructure;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Thomas Wendzinski
 */
@Component
public class DomainServiceLocator implements ApplicationContextAware{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(DomainServiceLocator.class);
    private static DomainServiceLocator instance;
    private ApplicationContext ctx;
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        DomainServiceLocator.instance = this;
    }
    
    protected static DomainServiceLocator instance(){
        if(instance == null) log.warn("instance was not initialized!");
        return instance;
    }
    public static <T> T getBean(Class<T> beanType){
        if(instance == null) return null;
        return instance.getBeanInternal(beanType);
    }
    public static <T> T getBean(Class<T> beanType, String name){
        if(instance == null) return null;
        return instance().ctx.getBean(name, beanType );
    }

    public <T> T getBeanInternal(Class<T> beanType){
        return this.ctx.getBean(beanType);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ctx = ac;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
