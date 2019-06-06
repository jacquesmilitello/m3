package io.m3.sql.csv;

public interface CsvReader extends AutoCloseable {

    CsvLine line();

}
