package io.m3.sql.model;

import com.google.common.collect.ImmutableList;
import io.m3.sql.Database;
import io.m3.sql.Descriptor;
import io.m3.sql.Dialect;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.sql.impl.DatabaseImpl;
import io.m3.sql.tx.TransactionManager;
import io.m3.sql.tx.TransactionManagerImpl;
import org.mockito.Mockito;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Pojos {

    private Pojos() {
    }

    // FOLDER
    public static final SqlTable FOLDER_TABLE = new SqlTable("folder", "a");
    public static final SqlPrimaryKey FOLDER_ID = new SqlPrimaryKey(FOLDER_TABLE, "id");
    public static final SqlPrimaryKey FOLDER_ID2 = new SqlPrimaryKey(FOLDER_TABLE, "id2");
    public static final SqlSingleColumn FOLDER_PARENT_FK = new SqlSingleColumn(FOLDER_TABLE, "parent_fk", false, true,
            true);
    public static final SqlSingleColumn FOLDER_PATH = new SqlSingleColumn(FOLDER_TABLE, "path", false, true, true);
    public static final SqlSingleColumn FOLDER_FULL_PATH = new SqlSingleColumn(FOLDER_TABLE, "full_path", false, true,
            true);
    public static final SqlSingleColumn FOLDER_CREATED_AT = new SqlSingleColumn(FOLDER_TABLE, "created_at", false,
            true, true);
    public static final SqlSingleColumn FOLDER_CREATED_BY = new SqlSingleColumn(FOLDER_TABLE, "created_by", false,
            false, false);
    public static final com.google.common.collect.ImmutableList<SqlColumn> FOLDER_ALL = com.google.common.collect.ImmutableList
            .of(FOLDER_ID, FOLDER_PARENT_FK, FOLDER_PATH, FOLDER_FULL_PATH, FOLDER_CREATED_AT, FOLDER_CREATED_BY);
    public static final com.google.common.collect.ImmutableList<SqlSingleColumn> FOLDER_COLUMNS = com.google.common.collect.ImmutableList
            .of(FOLDER_PARENT_FK, FOLDER_PATH, FOLDER_FULL_PATH, FOLDER_CREATED_AT, FOLDER_CREATED_BY);

    public static final com.google.common.collect.ImmutableList<SqlPrimaryKey> FOLDER_IDS = com.google.common.collect.ImmutableList
            .of(FOLDER_ID);

    public static final com.google.common.collect.ImmutableList<SqlPrimaryKey> FOLDER_IDS_MULTIPLE = com.google.common.collect.ImmutableList
            .of(FOLDER_ID, FOLDER_ID2);
    // WORK
    public static final SqlTable WORK_TABLE = new SqlTable("work", "b");
    public static final SqlPrimaryKey WORK_ID = new SqlPrimaryKey(WORK_TABLE, "id");
    public static final SqlSingleColumn WORK_FOLDER_FK = new SqlSingleColumn(WORK_TABLE, "folder_fk", false, true,
            true);
    public static final SqlSingleColumn WORK_UBI = new SqlSingleColumn(WORK_TABLE, "ubi", false, true, true);

    // SEMANTIC
    public static final SqlTable SEMANTIC_TABLE = new SqlTable("semantic", "s");
    public static final SqlColumn SEMANTIC_WORK_FK = new SqlSingleColumn(SEMANTIC_TABLE, "work_fk", false, true, true);


    public static final Descriptor DESCRIPTOR_FOLDER = new Descriptor() {
        public ImmutableList<SqlSingleColumn> columns() {
            return FOLDER_COLUMNS;
        }

        @Override
        public ImmutableList<SqlPrimaryKey> ids() {
            return FOLDER_IDS;
        }

        @Override
        public SqlTable table() {
            return FOLDER_TABLE;
        }

    };

    public static final Descriptor DESCRIPTOR_WORK = new Descriptor() {
        public ImmutableList<SqlSingleColumn> columns() {
            return null;
        }

        @Override
        public ImmutableList<SqlPrimaryKey> ids() {
            return ImmutableList.of(WORK_ID);
        }

        @Override
        public SqlTable table() {
            return WORK_TABLE;
        }

    };

    public static final Descriptor DESCRIPTOR_SEMANTIC = new Descriptor() {
        public ImmutableList<SqlSingleColumn> columns() {
            return null;
        }

        @Override
        public ImmutableList<SqlPrimaryKey> ids() {
            return null;
        }

        @Override
        public SqlTable table() {
            return SEMANTIC_TABLE;
        }

    };

    public static final Module MODULE_STORAGE = new Module("storage", DESCRIPTOR_FOLDER, DESCRIPTOR_WORK, DESCRIPTOR_SEMANTIC) {
    };

    public static final Database DATABASE = new DatabaseImpl(Mockito.mock(DataSource.class), H2,
            Mockito.mock(TransactionManager.class), "", MODULE_STORAGE);

    public static Database mockDatabase(DataSource dataSource) {
        return new DatabaseImpl(dataSource, Dialect.Name.H2,
                new TransactionManagerImpl(dataSource), "", MODULE_STORAGE);
    }

}
