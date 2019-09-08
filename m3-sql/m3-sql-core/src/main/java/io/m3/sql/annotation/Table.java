package io.m3.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Table {

    /**
     * Name for this sql table.
     */
    String value();

    /**
     * set this immutable or not.
     * if false (mutable table) -> generate {@link io.m3.sql.Repository} method ***forUpdate
     */
    boolean immutable() default false;

    /**
     * enable support of page in repository
     */
    boolean pageable() default false;
    
    
    Flyway flyway() default @Flyway(version = "", description = "");
    

}