package io.m3.sql.expression;

import io.m3.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Expression {

    String build(Dialect dialect, String alias);

}