package io.m3.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitionReadWrite implements TransactionDefinition {
    @Override
    public boolean isReadOnly() {
        return false;
    }
}
