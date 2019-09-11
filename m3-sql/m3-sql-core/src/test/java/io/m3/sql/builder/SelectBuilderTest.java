package io.m3.sql.builder;

import io.m3.sql.Database;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.test.LoggerInstancePostProcessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static com.google.common.collect.ImmutableList.of;
import static io.m3.sql.dialect.Dialects.h2;
import static io.m3.sql.expression.Expressions.and;
import static io.m3.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(LoggerInstancePostProcessor.class)
class SelectBuilderTest {

    private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");
    private static final SqlTable TABLE_T2 = new SqlTable("T2", "b");
    private static final SqlTable TABLE_T3 = new SqlTable("T3", "c");

    private static final SqlColumn COL_T1_01 = new SqlSingleColumn(TABLE_T1, "col_T1_01", false, true, true);
    private static final SqlColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true, true);
    private static final SqlColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", false, true, true);

    private static final SqlColumn COL_T2_01 = new SqlSingleColumn(TABLE_T2, "col_T2_01", false, true, true);
    private static final SqlColumn COL_T2_02 = new SqlSingleColumn(TABLE_T2, "col_T2_02", false, true, true);
    private static final SqlColumn COL_T2_03 = new SqlSingleColumn(TABLE_T2, "col_T2_03", false, true, true);

    private static final SqlColumn COL_T3_01 = new SqlSingleColumn(TABLE_T3, "col_T3_01", false, true, true);
    private static final SqlColumn COL_T3_02 = new SqlSingleColumn(TABLE_T3, "col_T3_02", false, true, true);
    private static final SqlColumn COL_T3_03 = new SqlSingleColumn(TABLE_T3, "col_T3_03", false, true, true);

    private Database database;

    @BeforeEach
    void before() {
        database = Mockito.mock(Database.class);
        Module module = mock(Module.class);
        when(database.isMonoSchema()).thenReturn(true);
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);
        when(database.getModule(TABLE_T2)).thenReturn(module);
        when(database.getModule(TABLE_T3)).thenReturn(module);
    }


    @Test
    void testSelect() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1", builder.build());
    }

    @Test
    void testSelectLimit() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.limit(5);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 LIMIT 5", builder.build());
    }

    @Test
    void testSelectForUpdate() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.forUpdate();
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 FOR UPDATE", builder.build());
    }

    @Test
    void testSelectOrderBy() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.orderBy(Order.asc(COL_T1_01));
        builder.from(TABLE_T1);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC", builder.build());

        builder.orderBy(Order.desc(COL_T1_02));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC,col_T1_02 DESC", builder.build());
    }

    @Test
    void testSelectWhere() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(eq(COL_T1_02));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE col_T1_02=?", builder.build());

        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(and(eq(COL_T1_02), eq(COL_T1_03)));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE (col_T1_02=? AND col_T1_03=?)", builder.build());
    }

    @Test
    void testLeftJoin() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.leftJoin(TABLE_T2, COL_T2_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 AS a LEFT JOIN T2 AS b ON b.col_T2_01=a.col_T1_01", builder.build());
        builder.leftJoin(TABLE_T3, COL_T3_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 AS a LEFT JOIN T2 AS b ON b.col_T2_01=a.col_T1_01 LEFT JOIN T3 AS c ON c.col_T3_01=a.col_T1_01", builder.build());
    }

    @Test
    void testLeftJoinAlias() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1, TABLE_T2);
        builder.leftJoin(TABLE_T3, COL_T3_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 AS a, T2 AS b LEFT JOIN T3 AS c ON c.col_T3_01=a.col_T1_01", builder.build());
    }

    @Test
    void testLeftJoinChecks() {

        // without from
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        try {
            builder.leftJoin(TABLE_T2, COL_T2_01, COL_T1_01);
            assertTrue(false);
        } catch (SelectBuilderException cause) {
            assertTrue(true);
        }


        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02));
        builder.from(TABLE_T1, TABLE_T2);
        try {
            builder.leftJoin(TABLE_T2, COL_T2_01, TABLE_T2, COL_T2_01);
            assertTrue(false);
        } catch (SelectBuilderException cause) {
            assertTrue(true);
        }

    }
}