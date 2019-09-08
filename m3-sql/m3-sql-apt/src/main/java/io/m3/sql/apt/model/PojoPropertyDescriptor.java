package io.m3.sql.apt.model;

import static io.m3.sql.apt.Helper.isPrimitiveType;
import static io.m3.sql.apt.Helper.propertyName;

import javax.lang.model.element.ExecutableElement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PojoPropertyDescriptor {

    private static int COUNTER = 0;

    private final String name;
    private final String variable;
    private final ExecutableElement getter;
    private ExecutableElement setter;

    public PojoPropertyDescriptor(ExecutableElement getter) {
        this.name = propertyName(getter.getSimpleName().subSequence(3, getter.getSimpleName().length()).toString());
        this.getter = getter;
        if (isPrimitiveType(this.name)) {
            this.variable = new StringBuilder().append('_').append(this.name).append(++COUNTER).toString();
        } else {
            this.variable = name;
        }
    }

    public String name() {
        return this.name;
    }

    public void setSetter(ExecutableElement setter) {
        this.setter = setter;
    }

    public ExecutableElement getter() {
        return this.getter;
    }

    public ExecutableElement setter() {
        return this.setter;
    }

    public String variable() {
       return this.variable;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PojoPropertyDescriptor [name=");
		builder.append(name);
		builder.append(", variable=");
		builder.append(variable);
		builder.append(", getter=");
		builder.append(getter);
		builder.append(", setter=");
		builder.append(setter);
		builder.append("]");
		return builder.toString();
	}
    
    
}
