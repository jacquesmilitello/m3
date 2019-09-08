package io.m3.sql.tx;

import io.m3.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitionReadOnly implements TransactionDefinition {

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isReadOnly", true)
                .toString();
    }

}
