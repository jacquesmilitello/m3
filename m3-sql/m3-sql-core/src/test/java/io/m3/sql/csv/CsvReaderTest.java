package io.m3.sql.csv;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static io.m3.sql.csv.CsvColumnConverters.RAW_STRING;
import static io.m3.sql.csv.CsvReaders.newReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvReaderTest {

    @Test
    void readAddresses() throws Exception {

        try (FileChannel fc = createFileChannel("/csv/addresses.csv")) {

            try (CsvReader reader = newReader(fc)) {

                // line 0
                CsvLine line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(0, line.line());
                assertEquals("John", line.get(0, RAW_STRING));
                assertEquals("Doe", line.get(1, RAW_STRING));
                assertEquals("120 jefferson st.", line.get(2, RAW_STRING));
                assertEquals("Riverside", line.get(3, RAW_STRING));
                assertEquals(" NJ", line.get(4, RAW_STRING));
                assertEquals(" 08075", line.get(5, RAW_STRING));

                // line 1
                line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(1, line.line());
                assertEquals("Jack", line.get(0, RAW_STRING));
                assertEquals("McGinnis", line.get(1, RAW_STRING));
                assertEquals("220 hobo Av.", line.get(2, RAW_STRING));
                assertEquals("Phila", line.get(3, RAW_STRING));
                assertEquals(" PA", line.get(4, RAW_STRING));
                assertEquals("09119", line.get(5, RAW_STRING));

                // line 2
                line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(2, line.line());
                assertEquals("\"John \"\"Da Man\"\"\"", line.get(0, RAW_STRING));
                assertEquals("Repici", line.get(1, RAW_STRING));
                assertEquals("120 Jefferson St.", line.get(2, RAW_STRING));
                assertEquals("Riverside", line.get(3, RAW_STRING));
                assertEquals(" NJ", line.get(4, RAW_STRING));
                assertEquals("08075", line.get(5, RAW_STRING));

                // line 3
                line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(3, line.line());
                assertEquals("Stephen", line.get(0, RAW_STRING));
                assertEquals("Tyler", line.get(1, RAW_STRING));
                assertEquals("\"7452 Terrace \"\"At the Plaza\"\" road\"", line.get(2, RAW_STRING));
                assertEquals("SomeTown", line.get(3, RAW_STRING));
                assertEquals("SD", line.get(4, RAW_STRING));
                assertEquals(" 91234", line.get(5, RAW_STRING));

                // line 4
                line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(4, line.line());
                assertEquals("", line.get(0, RAW_STRING));
                assertEquals("Blankman", line.get(1, RAW_STRING));
                assertEquals("", line.get(2, RAW_STRING));
                assertEquals("SomeTown", line.get(3, RAW_STRING));
                assertEquals(" SD", line.get(4, RAW_STRING));
                assertEquals(" 00298", line.get(5, RAW_STRING));


                // line 5
                line = reader.line();
                assertEquals(6, line.columns());
                assertEquals(5, line.line());
                assertEquals("\"Joan \"\"the bone\"\", Anne\"", line.get(0, RAW_STRING));
                assertEquals("Jet", line.get(1, RAW_STRING));
                assertEquals("\"9th, at Terrace plc\"", line.get(2, RAW_STRING));
                assertEquals("Desert City", line.get(3, RAW_STRING));
                assertEquals("CO", line.get(4, RAW_STRING));
                assertEquals("00123", line.get(5, RAW_STRING));
            }
        }

    }

    private FileChannel createFileChannel(String file) throws IOException, URISyntaxException {
        Path path = Paths.get(CsvReaderTest.class.getResource(file).toURI());
        return FileChannel.open(path, StandardOpenOption.READ);
    }
}
