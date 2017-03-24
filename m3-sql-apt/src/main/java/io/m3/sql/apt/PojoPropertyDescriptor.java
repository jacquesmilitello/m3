package io.m3.sql.apt;

import javax.lang.model.element.ExecutableElement;

import static io.m3.sql.apt.Helper.variable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoPropertyDescriptor {

    private final String name;
    private final ExecutableElement getter;
    private ExecutableElement setter;

    public PojoPropertyDescriptor(ExecutableElement getter) {
        this.name = variable(getter.getSimpleName().subSequence(3, getter.getSimpleName().length()).toString());
        this.getter = getter;
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
}
