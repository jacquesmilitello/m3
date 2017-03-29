package io.m3.sql.apt;

import javax.lang.model.element.ExecutableElement;

import static io.m3.sql.apt.Helper.isPrimitiveType;
import static io.m3.sql.apt.Helper.propertyName;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoPropertyDescriptor {

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
}
