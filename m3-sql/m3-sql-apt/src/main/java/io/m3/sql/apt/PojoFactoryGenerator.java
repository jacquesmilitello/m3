package io.m3.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import io.m3.sql.apt.model.PojoDescriptor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.m3.sql.apt.Helper.*;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoFactoryGenerator implements Generator {

    public void generate(ProcessingEnvironment env, List<PojoDescriptor> descriptors, Map<String, Object> properties) {
        Map<String, List<PojoDescriptor>> packages = analyse(env, descriptors);
        for (Map.Entry<String, List<PojoDescriptor>> entry : packages.entrySet()) {
            try {
                create(env, entry.getKey(), entry.getValue());
            } catch (IOException cause) {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR, "PojoFactoryGenerator -> IOException for [" + entry + "] -> [" + cause.getMessage() + "]");
            }
        }
    }



    private void create(ProcessingEnvironment env, String pack, List<PojoDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Factory");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, PojoFactoryGenerator.class.getName());

        writer.write("public final class Factory {");
        writeNewLine(writer);

        for (PojoDescriptor desc : descriptors) {
            writeNewLine(writer);
            writer.write("    public static final ");
            writer.write(desc.element().toString());
            writer.write(" new");
            writer.write(desc.element().getSimpleName().toString());
            writer.write("() {");
            writeNewLine(writer);
            writer.write("        ");
            writer.write("return new ");
            writer.write(desc.element().getSimpleName().toString() + "Impl");
            writer.write("();");
            writeNewLine(writer);
            writer.write("    }");
            writeNewLine(writer);
        }

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }
}
