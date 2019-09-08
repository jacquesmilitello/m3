package io.m3.sql.expression;

import io.m3.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface AggregateFunction {

    String build(Dialect dialect, boolean alias);
}
