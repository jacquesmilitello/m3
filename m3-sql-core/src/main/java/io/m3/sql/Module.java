package io.m3.sql;

import io.m3.sql.desc.SqlTable;
import io.m3.util.ImmutableList;
import io.m3.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class Module {

    /**
     * Name of this module.
     */
    private final String name;

    /**
     * Name of this catalog.
     */
    private final String catalog;

    /**
     * All Sql Tables for this modules
     */
    private final ImmutableList<Descriptor> descriptors;

    public Module(String name, String catalog, Descriptor ...  descriptors) {
        this.name = name;
        this.catalog = catalog;
        this.descriptors = ImmutableList.of(descriptors);
    }

    public final String name() {
        return this.name;
    }

    public final String catalog() {
        return this.catalog;
    }

    public final ImmutableList<Descriptor> descriptors() {
        return this.descriptors;
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this).append("name", this.name).append("catalog", this.catalog).append("descriptors", this.descriptors).toString();
    }

}