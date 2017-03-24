package io.m3.sql.expression;


import io.m3.sql.Dialect;
import io.m3.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleExpression implements Expression {

    /**
     * Operation for this SQL (=, <, >, >=, <=).
     */
    private final String operation;

    /**
     * Name of this column.
     */
    private final SqlColumn column;
    
    /**
     * value
     */
    private final String value;

    public SimpleExpression(SqlColumn column, String operation) {
        this(column, operation, null);
    }
    
    public SimpleExpression(SqlColumn column, String operation, String value) {
        this.operation = operation;
        this.column = column;
        this.value = value;
    }

//    /** {@inheritDoc} */
//    @Override
//    public String build() {
//        return build(new StringBuilder(32).append('`').append(column.getTable().name()).append('`').toString());
//    }

    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, String alias) {
    	StringBuilder builder =  new StringBuilder(32);

    	if (alias != null) {
            builder.append(alias).append('.');
        }

    	builder.append(column.name()).append(operation);
    	if (value == null) {
    		builder.append('?');	
    	} else {
    		builder.append('\'').append(value).append('\'');
    	}
    	return builder.toString();
    }

}