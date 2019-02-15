package io.m3.sql.tx;

import io.m3.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitionReadWrite implements TransactionDefinition {

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isReadOnly", false)
                .toString();
    }
}
