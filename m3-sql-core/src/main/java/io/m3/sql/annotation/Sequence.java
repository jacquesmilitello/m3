package io.m3.sql.annotation;

import io.m3.sql.id.Identifier;
import io.m3.sql.id.NoIdentifierGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

@Target({METHOD})
@Retention(RUNTIME)
public @interface Sequence {

    /**
     * sequence name.
     */
    String value();


}