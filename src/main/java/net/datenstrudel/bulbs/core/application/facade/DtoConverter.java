package net.datenstrudel.bulbs.core.application.facade;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A Type conversion specification for the purpose of abstractting domain (model) 
 * internal types from external types that might be required/prefered to be used 
 * outside the 'Application Layer' for communication with clients by DTOs.
 * ('Application Layer' as inner Hexagon in Hexagonal Architecture. )
 * @param <I> Domain Object type
 * @param <O> Dto type
 * @author Thomas Wendzinski
 */
public interface DtoConverter<I, O> {

    /**
     * Converts domain object to a <code>Dto</code>
     * @param src
     * @return 
     */
    public O convert(I src);
    /**
     * Convert the Dto back to a domain object.
     * @param dto
     * @return
     */
    public I reverseConvert(O dto);
    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * @return what specific output type of DTO does <code>this</code> support
     */
    public Class<O> supportedDtoClass();
    public Class<I> supportedDomainClass();

    public default Collection<I> reverseConvertCollection(Collection<O> in){
        try {
            Collection res = in.getClass().newInstance();
            res.addAll(in.stream().map(this::reverseConvert).collect(Collectors.toList()));
            return res;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    public default Collection<O> convertCollection(Collection<I> in){
        try {
            Collection res = in.getClass().newInstance();
            res.addAll(in.stream().map(this::convert).collect(Collectors.toList()));
            return res;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
