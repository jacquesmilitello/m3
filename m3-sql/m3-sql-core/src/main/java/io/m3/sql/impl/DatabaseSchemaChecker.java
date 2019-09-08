package io.m3.sql.impl;


import io.m3.sql.Descriptor;
import io.m3.sql.Module;
import io.m3.sql.desc.SqlPrimaryKey;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.sql.desc.SqlTable;
import io.m3.util.Strings;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseSchemaChecker {

    private DatabaseSchemaChecker() {
    }


    static void checkModule(StringBuilder builder, DatabaseMetaData meta, Module module) throws SQLException {

        builder.append("\nModule   -> name : [").append(module.name()).append(']');
        for (Descriptor descriptor : module.descriptors()) {
            builder.append("\n         \t-> table : [").append(descriptor.table().name()).append("] -> alias [").append(descriptor.table().alias()).append("] -> exists [");
            builder.append(checkTable(meta, module, descriptor.table())).append(']');

            for (SqlPrimaryKey key : descriptor.ids()) {
                builder.append("\n         \t\t-> id : [").append(key.name()).append("] -> exists [");
                builder.append(checkColumn(meta, module, descriptor.table(), key)).append(']');
            }

            for (SqlSingleColumn column : descriptor.columns()) {
                builder.append("\n         \t\t-> column : [").append(column.name()).append("] -> exists [");
                builder.append(checkColumn(meta, module, descriptor.table(), column)).append(']');
            }
        }

    }

    private static boolean checkColumn(DatabaseMetaData meta, Module module, SqlTable table, SqlPrimaryKey key) throws SQLException {
        String catalog = Strings.isEmpty(module.catalog()) ? null : module.catalog().toUpperCase();
        try (ResultSet res = meta.getColumns(catalog, null, table.name().toUpperCase(), key.name().toUpperCase() )) {
            if (res.next()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean checkColumn(DatabaseMetaData meta, Module module, SqlTable table, SqlSingleColumn column) throws SQLException {
        String catalog = Strings.isEmpty(module.catalog()) ? null : module.catalog().toUpperCase();
        try (ResultSet res = meta.getColumns(catalog, null, table.name().toUpperCase(), column.name().toUpperCase() )) {
            if (res.next()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean checkTable(DatabaseMetaData meta, Module module, SqlTable table) throws SQLException {
        String catalog = Strings.isEmpty(module.catalog()) ? null : module.catalog().toUpperCase();
        try (ResultSet res = meta.getTables(catalog, null, table.name().toUpperCase(), null )) {
            if (res.next()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
