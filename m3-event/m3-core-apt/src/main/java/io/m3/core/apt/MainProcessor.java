package io.m3.core.apt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.m3.core.annotation.CqrsCommand")
public class MainProcessor extends AbstractProcessor {

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
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "MainProcessor done");
            return false;
        }
        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "MainProcessor start");

        doProcess(roundEnv);

        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "MainProcessor end");

        this.firstTime = true;
        return true;
    }

    private void doProcess(RoundEnvironment roundEnv) {

    }
}
