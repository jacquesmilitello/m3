package io.m3.sql.jdbc;

import java.sql.PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface DeleteMapper<T> {


    void delete(PreparedStatement ps, T pojo);
}
