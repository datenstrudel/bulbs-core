package net.datenstrudel.bulbs.core.application.facade;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ModelFacadeOutPort{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(ModelFacadeOutPort.class);
    private static final ThreadLocal<Set<? extends DtoConverter>> CONVERTERS
            = new ThreadLocal<Set<? extends DtoConverter>>(){
        @Override
        protected Set<? extends DtoConverter> initialValue() {
            return Sets.newHashSet();
//            return new HashSet<>();
        }
    };
    private static final ThreadLocal<Boolean> CONVERTING 
            = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private static final ThreadLocal<Map<Class, Object>> RESULTS
            = new ThreadLocal<Map<Class, Object>>(){
        @Override
        protected Map initialValue() {
            return Maps.newHashMap();
        }
    };
    private static final ThreadLocal<Map<Class, Collection>> RESULTS_COLLECTIONS
            = new ThreadLocal<Map<Class, Collection>>(){
        @Override
        protected Map initialValue() {
            return Maps.newHashMap();
        }
    };
    @Autowired
    private DtoConverterRegistry converterFactory;
    //~ Construction ///////////////////////////////////////////////////////////
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public void write(Object domainObject){
        if(domainObject == null)return;
        if (CONVERTING.get()){
            return ;
        }
        try{
            CONVERTING.set(Boolean.TRUE);
            Set<DtoConverter> converters = (Set<DtoConverter>) CONVERTERS.get();
            converters.stream()
                    .filter( (el) ->  el.supportedDomainClass().isAssignableFrom(domainObject.getClass())  )
                    .forEach( (el) -> RESULTS.get().put( el.supportedDtoClass(), el.convert(domainObject) )
            );
        }finally{
            CONVERTING.set(Boolean.FALSE);
        }
    }    
    public void write(Collection domainCollection){
        if (CONVERTING.get()){
            return ;
        }
        try{
            CONVERTING.set(Boolean.TRUE);
            Set<DtoConverter> converters = (Set<DtoConverter>) CONVERTERS.get();
            for (DtoConverter converter : converters) {
                if(domainCollection.isEmpty()){
                    continue;
                }
                if( converter.supportedDomainClass().isAssignableFrom(
                        domainCollection.iterator().next().getClass()) ){
                    Collection results;
                    try {
                        results = domainCollection.getClass().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                    for (Object el : domainCollection) {
                        results.add(converter.convert(el));
                    }
                    RESULTS_COLLECTIONS.get().put(
                            converter.supportedDtoClass(),
                            results);
                }
            }
        }finally{
            CONVERTING.set(Boolean.FALSE);
        }
    }

    public void registerConverterFor(Class... c){
        if (CONVERTING.get())return ;
        this.reset();
        Set<DtoConverter> knownConverters;
        knownConverters = (Set) CONVERTERS.get();
        knownConverters.addAll(converterFactory.convertersFor(c));
    }
    public <T> T outputAs(Class<T> c){
        return (T) RESULTS.get().get(c);
    }
    public <T> Collection<T> outputAsCollection(Class<T> c){
        return (Collection) RESULTS_COLLECTIONS.get().get(c);
    }
    public <T> List<T> outputAsList(Class<T> c){
        return (List) RESULTS_COLLECTIONS.get().get(c);
    }
    public <T> Set<T> outputAsSet(Class<T> c){
        return (Set) RESULTS_COLLECTIONS.get().get(c);
    }
    
    public ModelFacadeOutPort reset(){
        if(!CONVERTING.get())CONVERTERS.get().clear();
        this.RESULTS.get().clear();
        this.CONVERTERS.get().clear();
        this.RESULTS_COLLECTIONS.get().clear();
        return this;
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
