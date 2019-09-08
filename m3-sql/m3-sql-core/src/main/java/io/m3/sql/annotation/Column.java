package io.m3.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface Column {

    /**
     * Name of the column.
     */
    String value();

    /**
     * Optional, this column is nullable. (only if type is an object and not a primitive).
     *
     * @return true if the column is nullable.
     */
    boolean nullable() default false;

    /**
     * Optional, the column is included in SQL INSERT statements.
     */
    boolean insertable() default true;

    /**
     * Optional,the column is included in SQL UPDATE statement.
     */
    boolean updatable() default true;
    
    /**
     * (Optional) The column length. (Applies only if a string-valued column is used.)
     */
    int length() default 255;
    
}