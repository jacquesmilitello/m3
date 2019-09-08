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
public @interface PrimaryKey {

    /**
     * Name of the column for this primary key.
     */
    String value();

    @SuppressWarnings("rawtypes")
	Class< ? extends Identifier> generator() default NoIdentifierGenerator.class;

}