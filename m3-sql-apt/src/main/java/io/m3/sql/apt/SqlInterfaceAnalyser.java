package io.m3.sql.apt;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.m3.sql.apt.Helper.variable;

final class SqlInterfaceAnalyser implements Function<Element, PojoDescriptor> {

    private final Messager messager;

    SqlInterfaceAnalyser(Messager messager) {
        this.messager = messager;
    }

    @Override
    public PojoDescriptor apply(Element element) {

        if (ElementKind.INTERFACE != element.getKind()) {
            this.messager.printMessage(Diagnostic.Kind.ERROR,"element [" + element + "] should be an interface");
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
                setters.add(executableElement);
                continue;
            }

            if (executableElement.getSimpleName().toString().startsWith("get")) {
                PrimaryKey primaryKey =  executableElement.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    ids.add(new PojoPropertyDescriptor(executableElement));
                    continue;
                }
                Column column = executableElement.getAnnotation(Column.class);
                if (column == null) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "getter [" + method + "] in [" + element + "] should have @Column.");
                } else {
                    ppds.add(new PojoPropertyDescriptor(executableElement));
                }
                continue;
            }

            this.messager.printMessage(Diagnostic.Kind.ERROR, "method [" + method + "] in [" + element + "] should start with [set] or [get] prefix");
        }

        setters.forEach( s -> {
            String property = variable(s.getSimpleName().subSequence(3,s.getSimpleName().length()).toString());
            Optional<PojoPropertyDescriptor> ppd =  ppds.stream().filter(p -> p.name().equals(property)).findFirst();
            if (!ppd.isPresent()) {
                Optional<PojoPropertyDescriptor> id =  ids.stream().filter(p -> p.name().equals(property)).findFirst();
                if (!id.isPresent()) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, "setter [" + s + "] in [" + element + "] is not link to a getter");
                } else {
                    id.get().setSetter(s);
                }
            } else {
                ppd.get().setSetter(s);
            }
        });

        return new PojoDescriptor(element, ids, ppds);
    }


}