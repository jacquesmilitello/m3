package io.m3.sql.apt.ex004;


import io.m3.sql.Database;
import io.m3.sql.Dialect;
import io.m3.sql.csv.CsvColumnConverters;
import io.m3.sql.csv.CsvLine;
import io.m3.sql.csv.CsvReader;
import io.m3.sql.domain.Page;
import io.m3.sql.domain.Pageable;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.Transaction;
import io.m3.sql.tx.TransactionManagerImpl;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

import static io.m3.sql.csv.CsvReaders.newReader;

class LoadTest {


    private static final Function<byte[], LocalDate> LOCAL_DATE_FUNCTION = CsvColumnConverters.date(DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH));

    private JdbcConnectionPool ds;
    private Database database;

    @BeforeEach
    void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new io.m3.sql.apt.ex004.IoM3SqlAptEx004Module("ex004", ""));
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
    }

    @Test
    void load() throws Exception {

        try (FileChannel fc = createFileChannel("/csv/1000000 Sales Records.csv")) {
            try (CsvReader reader = newReader(fc)) {

                // read header
                CsvLine line = reader.line();

                SalesRecordAbstractRepository recordAbstractRepository = new SalesRecordAbstractRepository(database) {
                };

                int i = 0;
                try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
                    while ((line = reader.line()) != null) {

                        SalesRecord record = Factory.newSalesRecord();
                        record.setId(line.line());
                        record.setRegion(line.get(0, CsvColumnConverters.RAW_STRING));
                        record.setCountry(line.get(1, CsvColumnConverters.RAW_STRING));
                        record.setItemType(line.get(2, CsvColumnConverters.RAW_STRING));
                        record.setSales(line.get(3, CsvColumnConverters.RAW_STRING));
                        record.setOrderPriority(line.get(4, CsvColumnConverters.RAW_STRING));
                        record.setOrderDate(Date.valueOf(line.get(5, LOCAL_DATE_FUNCTION)));
                        record.setOrderId(Long.valueOf( line.get(6, CsvColumnConverters.RAW_STRING)));
                        record.setShipDate(Date.valueOf(line.get(7, LOCAL_DATE_FUNCTION)));
                        record.setUnitSold(Double.valueOf( line.get(8, CsvColumnConverters.RAW_STRING)));
                        record.setUnitPrice(Double.valueOf( line.get(9, CsvColumnConverters.RAW_STRING)));
                        record.setUnitCost(Double.valueOf( line.get(10, CsvColumnConverters.RAW_STRING)));
                        record.setTotalRevenue(Double.valueOf( line.get(11, CsvColumnConverters.RAW_STRING)));
                        record.setTotalCost(Double.valueOf( line.get(12, CsvColumnConverters.RAW_STRING)));
                        record.setTotalProfit(Double.valueOf( line.get(13, CsvColumnConverters.RAW_STRING)));
                        recordAbstractRepository.insert(record);

                        i++;
                        if (i == 101) {
                            break;
                        }
                    }

                    tx.commit();
                }

                try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {

                    Page<SalesRecord> page = recordAbstractRepository.page(Pageable.of(0, 5));

                    Assertions.assertEquals(101, page.getTotalElements());
                    Assertions.assertEquals(21, page.getTotalPages());

                    for (int j = 0 ; j < page.getTotalPages(); j++) {
                        System.out.println("Page " + j);
                        page.getContent().forEach(System.out::println);
                        if (j + 1 < page.getTotalPages()) {
                            page = recordAbstractRepository.page(page.next());
                        }
                    }
                }
            }
        }
    }

    private FileChannel createFileChannel(String file) throws IOException, URISyntaxException {
        Path path = Paths.get(LoadTest.class.getResource(file).toURI());
        return FileChannel.open(path, StandardOpenOption.READ);
    }
}
