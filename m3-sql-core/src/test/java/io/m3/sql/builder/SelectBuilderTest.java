package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.TestDialect;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import static io.m3.sql.expression.Expressions.eq;
import static io.m3.sql.expression.Expressions.and;
import static io.m3.util.ImmutableList.of;
import static org.junit.Assert.assertTrue;

public class SelectBuilderTest {

    private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");
    private static final SqlTable TABLE_T2 = new SqlTable("T2", "b");
    private static final SqlTable TABLE_T3 = new SqlTable("T3", "c");

    private static final SqlColumn COL_T1_01 = new SqlSingleColumn(TABLE_T1, "col_T1_01", false, true,true);
    private static final SqlColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true,true);
    private static final SqlColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", false, true,true);

    private static final SqlColumn COL_T2_01 = new SqlSingleColumn(TABLE_T2, "col_T2_01", false, true,true);
    private static final SqlColumn COL_T2_02 = new SqlSingleColumn(TABLE_T2, "col_T2_02", false, true,true);
    private static final SqlColumn COL_T2_03 = new SqlSingleColumn(TABLE_T2, "col_T2_03", false, true,true);

    private static final SqlColumn COL_T3_01 = new SqlSingleColumn(TABLE_T3, "col_T3_01", false, true,true);
    private static final SqlColumn COL_T3_02 = new SqlSingleColumn(TABLE_T3, "col_T3_02", false, true,true);
    private static final SqlColumn COL_T3_03 = new SqlSingleColumn(TABLE_T3, "col_T3_03", false, true,true);

    private Database database;

    @Before
    public void before() {
        database = Mockito.mock(Database.class);
        Mockito.when(database.isMonoSchema()).thenReturn(true);
        Mockito.when(database.dialect()).thenReturn(new TestDialect());
    }


    @Test
    public void testSelect() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1", builder.build());
    }

    @Test
    public void testSelectLimit() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.limit(5);
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 LIMIT 5", builder.build());
    }

    @Test
    public void testSelectForUpdate() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.forUpdate();
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 FOR UPDATE", builder.build());
    }

    @Test
    public void testSelectOrderBy() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.orderBy(Order.asc(COL_T1_01));
        builder.from(TABLE_T1);
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC", builder.build());

        builder.orderBy(Order.desc(COL_T1_02));
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC,col_T1_02 DESC", builder.build());
    }

    @Test
    public void testSelectWhere() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(eq(COL_T1_02));
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE col_T1_02=?", builder.build());

        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(and(eq(COL_T1_02),eq(COL_T1_03)));
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE (col_T1_02=? AND col_T1_03=?)", builder.build());
    }

    @Test
    public void testLeftJoin() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.leftJoin(TABLE_T2, COL_T2_01 , COL_T1_01);
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 LEFT JOIN T2 ON T2.col_T2_01=T1.col_T1_01", builder.build());
        builder.leftJoin(TABLE_T3, COL_T3_01 , COL_T1_01);
        Assert.assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 LEFT JOIN T2 ON T2.col_T2_01=T1.col_T1_01 LEFT JOIN T3 ON T3.col_T3_01=T1.col_T1_01", builder.build());
    }

    @Test
    public void testLeftJoinAlias() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1, TABLE_T2);
        builder.leftJoin(TABLE_T3, COL_T3_01 , COL_T1_01);
        Assert.assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 AS a, T2 AS b LEFT JOIN T3 ON T3.col_T3_01=a.col_T1_01", builder.build());
    }

    @Test
    public void testLeftJoinChecks() {

        // without from
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        try  {
            builder.leftJoin(TABLE_T2, COL_T2_01 , COL_T1_01);
            assertTrue(false);
        } catch (SelectBuilderException cause) {
            assertTrue(true);
        }


        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02 ));
        builder.from(TABLE_T1, TABLE_T2);
        try  {
            builder.leftJoin(TABLE_T2, COL_T2_01 , TABLE_T2, COL_T2_01);
            assertTrue(false);
        } catch (SelectBuilderException cause) {
            assertTrue(true);
        }

    }
}