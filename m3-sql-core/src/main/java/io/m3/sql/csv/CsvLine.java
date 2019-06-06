package io.m3.sql.csv;

import java.util.function.Function;

/**
 */
public interface CsvLine {

	 int line();

	 int columns();

	 byte[] get(int col);

	 <T> T get(int col, Function<byte[], T> converter);
}