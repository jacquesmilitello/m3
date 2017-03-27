package io.m3.sql;

import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlTable;

import java.io.IOException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class Dialect {


    public void wrap(Appendable appendable,SqlColumn column, boolean alias) {

        try {
            if (alias) {
                appendable.append(column.table().alias());
                appendable.append('.');
            }

            appendable.append(column.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wrap(Appendable appendable,SqlTable table, boolean alias) {
        try {
            appendable.append(table.name());
            if (alias) {
                appendable.append(alias());
                appendable.append(table.alias());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String alias() {
        return " AS ";
    }
}
