package io.m3.sql.csv;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.function.Function;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public final class CsvColumnConverters {

    private CsvColumnConverters() {
    }

    public static final Function<byte[], String> RAW_STRING = data -> new String(data, StandardCharsets.UTF_8);

    public static Function<byte[], LocalDate> date(DateTimeFormatter formatter) {
        return data -> formatter.parse(new String(data, StandardCharsets.UTF_8), LocalDate::from);
    }

}
