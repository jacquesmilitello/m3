package io.m3.sql.apt;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.m3.sql.apt.Helper.isPrimitiveType;
import static io.m3.sql.apt.Helper.propertyName;

final class SqlInterfaceAnalyser implements Function<Element, PojoDescriptor> {

    private final Messager messager;

    SqlInterfaceAnalyser(Messager messager) {
        this.messager = messager;
    }

    @Override
    public PojoDescriptor apply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, "element [" + element + "] should be an interface");
            return null;
        }

        List<PojoPropertyDescriptor> ppds = new ArrayList<>();
        List<PojoPropertyDescriptor> ids = new ArrayList<>();
        List<ExecutableElement> setters = new ArrayList<>();

        for (Element method : element.getEnclosedElements()) {

            if (ElementKind.METHOD != method.getKind()) {
                this.messager.printMessage(Diagnostic.Kind.ERROR, "element [" + method + "] in [" + element + "] is not a method, it's [" + element.getKind() + "]");
                return null;
            }

            ExecutableElement executableElement = (ExecutableElement) method;

            if (executableElement.getSimpleName().toString().startsWith("set")) {

                if (executableElement.getParameters().size() == 0) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "setter [" + executableElement + "] in [" + element + "] has 0 parameter !");
                }

                if (executableElement.getParameters().size() > 1) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "setter [" + executableElement + "] in [" + element + "] has more than 1 parameter !");
                }

                setters.add(executableElement);
                continue;
            }

            if (executableElement.getSimpleName().toString().startsWith("get")) {
                PrimaryKey primaryKey = executableElement.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    ids.add(new PojoPropertyDescriptor(executableElement));
                    continue;
                }
                Column column = executableElement.getAnnotation(Column.class);
                if (column == null) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "getter [" + method + "] in [" + element + "] should have @Column.");
                } else {

                    // check if column nullable.
                    if (column.nullable() && isPrimitiveType(executableElement.getReturnType().toString())) {
                        this.messager.printMessage(Diagnostic.Kind.ERROR, "getter [" + method + "] in [" + element + "] indicate a nullable type [" + executableElement.getReturnType() + "] -> it's not a nullable type.");
                    }


                    ppds.add(new PojoPropertyDescriptor(executableElement));
                }
                continue;
            }

            this.messager.printMessage(Diagnostic.Kind.ERROR, "method [" + method + "] in [" + element + "] should start with [set] or [get] prefix");
        }

        setters.forEach(s -> {
            String property = propertyName(s.getSimpleName().subSequence(3, s.getSimpleName().length()).toString());
            Optional<PojoPropertyDescriptor> ppd = ppds.stream().filter(p -> p.name().equals(property)).findFirst();
            if (!ppd.isPresent()) {
                Optional<PojoPropertyDescriptor> id = ids.stream().filter(p -> p.name().equals(property)).findFirst();
                if (!id.isPresent()) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "setter [" + s + "] in [" + element + "] is not link to a getter");
                } else {
                    checkGetterAndSetterParamerts(id.get(), s);
                    id.get().setSetter(s);
                }
            } else {
                checkGetterAndSetterParamerts(ppd.get(), s);
                ppd.get().setSetter(s);
            }
        });

        return new PojoDescriptor(element, ids, ppds);
    }


    private void checkGetterAndSetterParamerts(PojoPropertyDescriptor ppd, ExecutableElement setter) {
        if (!setter.getParameters().get(0).asType().toString().equals(ppd.getter().getReturnType().toString())) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, "setter [" + setter + "] in [" + setter.getEnclosingElement() + "] type error : getter=[" + ppd.getter().getReturnType() + "] and setter=[" + setter.getParameters().get(0).asType().toString() + "]");
        }
    }

}