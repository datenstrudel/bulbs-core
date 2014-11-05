package net.datenstrudel.bulbs.core.infrastructure.persistence;

import com.mongodb.DBObject;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Denotes a converter for Objects and MongoDBs {@link DBObject}s.
 * A class annotated with this type will be automatically used for object transformations.
 * @author Thomas Wendzinski
 */
@Target(value=TYPE)
@Retention(value=RUNTIME)
@Component
public @interface MongoConverter {

    String value() default "";
}
