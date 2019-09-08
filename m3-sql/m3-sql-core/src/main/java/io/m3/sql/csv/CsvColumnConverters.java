package io.m3.sql.csv;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public final class CsvColumnConverters {

    private CsvColumnConverters() {
    }

    public static final Function<byte[], String> RAW_STRING = data -> new String(data, StandardCharsets.UTF_8);

    public static Function<byte[], LocalDate> date(DateTimeFormatter formatter) {
        return data -> formatter.parse(new String(data, StandardCharsets.UTF_8), LocalDate::from);
    }

}
