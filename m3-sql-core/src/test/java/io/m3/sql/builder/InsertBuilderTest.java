package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.TestDialect;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static io.m3.util.ImmutableList.of;

public class InsertBuilderTest {

    private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");

    private static final SqlPrimaryKey COL_T1_01 = new SqlPrimaryKey(TABLE_T1, "col_T1_01");
    private static final SqlSingleColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true,true);
    private static final SqlSingleColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", true, true,true);
    private static final SqlSingleColumn COL_T1_04 = new SqlSingleColumn(TABLE_T1, "col_T1_04", true, false,true);
    private static final SqlSingleColumn COL_T1_05 = new SqlSingleColumn(TABLE_T1, "col_T1_05", true, true,false);


    private Database database;

    @Before
    public void before() {
        database = Mockito.mock(Database.class);
        Mockito.when(database.isMonoSchema()).thenReturn(true);
        Mockito.when(database.dialect()).thenReturn(new TestDialect());
    }


    @Test
    public void testInsert() {
        InsertBuilder builder = new InsertBuilder(database, TABLE_T1,  COL_T1_01, of(COL_T1_02, COL_T1_03,COL_T1_04, COL_T1_05));
        Assert.assertEquals("INSERT INTO T1 (`col_T1_01`,`col_T1_02`,`col_T1_03`,`col_T1_05`) VALUES (?,?,?,?)", builder.build());
    }
}
