package net.datenstrudel.bulbs.core.application.facade;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Automatically registers {@link DtoConverter}s by searching app context for beans of that
 * type and makes them available for clients.
 * @author Thomas Wendzinski
 */
@Component
public class DtoConverterRegistry {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(DtoConverterRegistry.class);
    private static DtoConverterRegistry instance;
    
    private Map<Class, DtoConverter> converters;
    private Map<Class, DtoConverter> convertersByDomain;
    @Autowired
    private ApplicationContext ctx;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public DtoConverterRegistry() {
    }
    public static DtoConverterRegistry instance(){
        if(instance == null)throw new IllegalStateException("DtoConverterRegistiry.instance was not initialized.");
        return instance;
    }
    
    @PostConstruct
    public void init(){
        this.instance = this;
        Map<String, DtoConverter> c = ctx.getBeansOfType(DtoConverter.class);
        converters = Maps.newHashMapWithExpectedSize(c.size());
        convertersByDomain = Maps.newHashMapWithExpectedSize(c.size());
        for (DtoConverter el : c.values()) {
            log.debug("Register DtoConverter for Dto Type '"
                    + el.supportedDtoClass()+"' and Domin Type '"
                    + el.supportedDomainClass()+"': " + el.getClass().getName());
            converters.put(el.supportedDtoClass(), el);
            convertersByDomain.put(el.supportedDomainClass(), el);
        }
        log.info("Count DtoConverters registered: {}", c.size());
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public <I,O> DtoConverter<I, O> converterForDomain(Class<I> domainOutType)
            throws ConverterNotFoundException{
        DtoConverter res = convertersByDomain.get(domainOutType);
        if(res == null)throw new ConverterNotFoundException("Converter missing for type: " + domainOutType);
        return res;
    }
    public <I,O> DtoConverter<I, O> converterFor(Class<O> dtoOutType)throws ConverterNotFoundException{
        DtoConverter res;
        
        if(Collection.class.isAssignableFrom(dtoOutType)) res = converters.get(Collection.class);
        else res = converters.get(dtoOutType);
        if(res == null)throw new ConverterNotFoundException("DtoConverter missing for domain type: " + dtoOutType);
        return res;
    }
    public Set<DtoConverter> convertersFor(Class... c)throws ConverterNotFoundException{
        Set<DtoConverter> res = Sets.newHashSetWithExpectedSize(5);
        for (Class el : c) {
            res.add(converterFor(el));
        }
        return res;
    }
    
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
