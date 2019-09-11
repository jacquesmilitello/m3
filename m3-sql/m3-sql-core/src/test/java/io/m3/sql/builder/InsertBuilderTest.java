package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.test.LoggerInstancePostProcessor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.google.common.collect.ImmutableList.of;
import static io.m3.sql.dialect.Dialects.h2;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(LoggerInstancePostProcessor.class)
class InsertBuilderTest {

    private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");

    private static final SqlPrimaryKey COL_T1_01 = new SqlPrimaryKey(TABLE_T1, "col_T1_01");
    private static final SqlSingleColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true, true);
    private static final SqlSingleColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", true, true, true);
    private static final SqlSingleColumn COL_T1_04 = new SqlSingleColumn(TABLE_T1, "col_T1_04", true, false, true);
    private static final SqlSingleColumn COL_T1_05 = new SqlSingleColumn(TABLE_T1, "col_T1_05", true, true, false);


    private Database database;

    @Test
    void testInsert() {
        database = mock(Database.class);
        Module module = mock(Module.class);
        when(database.isMonoSchema()).thenReturn(true);
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);

        InsertBuilder builder = new InsertBuilder(database, TABLE_T1, COL_T1_01, of(COL_T1_02, COL_T1_03, COL_T1_04, COL_T1_05));
        Assertions.assertEquals("INSERT INTO T1 (col_T1_01,col_T1_02,col_T1_03,col_T1_05) VALUES (?,?,?,?)", builder.build());
    }

    @Test
    void testInsertWithCatalog() {
        database = mock(Database.class);
        Module module = new Module("insert","to") {
        };
        when(database.isMonoSchema()).thenReturn(true);
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);


        InsertBuilder builder = new InsertBuilder(database, TABLE_T1, COL_T1_01, of(COL_T1_02, COL_T1_03, COL_T1_04, COL_T1_05));
        Assertions.assertEquals("INSERT INTO to.T1 (col_T1_01,col_T1_02,col_T1_03,col_T1_05) VALUES (?,?,?,?)", builder.build());
    }
}
