package io.m3.sql.jdbc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface MapperWithAutoIncrement<T> extends Mapper<T>, InsertMapperWithAutoIncrement<T> {

}