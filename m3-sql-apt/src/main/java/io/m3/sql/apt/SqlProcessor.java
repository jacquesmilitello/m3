package io.m3.sql.apt;

import io.m3.sql.annotation.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.m3.sql.annotation.Table")
public final class SqlProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnvironment;

    private boolean firstTime = false;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstTime) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor done");
            return false;
        }
        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor start");

        doProcess(roundEnv);

        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor end");

        this.firstTime = true;
        return true;
    }

    private void doProcess(RoundEnvironment roundEnvironment) {

        List<PojoDescriptor> descriptors = roundEnvironment.getElementsAnnotatedWith(Table.class)
                .stream()
                .map(new SqlInterfaceAnalyser(this.processingEnvironment.getMessager()))
                .collect(Collectors.toList());

        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "Number of Pojo(s) found : " + descriptors.size());

        new PojoImplementationGenerator().generate(this.processingEnv, descriptors);
        new PojoFactoryGenerator().generate(this.processingEnv, descriptors);
        new PojoDescriptorGenerator().generate(this.processingEnv, descriptors);
        new RepositoryGenerator().generate(this.processingEnv, descriptors);
        new MapperGenerator().generate(this.processingEnv, descriptors);
        new PojoMapperFactoryGenerator().generate(this.processingEnv, descriptors);

    }

}