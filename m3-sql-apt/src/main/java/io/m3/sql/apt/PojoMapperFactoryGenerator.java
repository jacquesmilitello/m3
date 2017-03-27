package io.m3.sql.apt;

import io.m3.sql.jdbc.Mapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static io.m3.sql.apt.Helper.*;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoMapperFactoryGenerator implements Generator {

    public void generate(ProcessingEnvironment env, List<PojoDescriptor> descriptors, Map<String, Object> properties) {
        Map<String, List<PojoDescriptor>> packages = analyse(env, descriptors);
        for (Map.Entry<String, List<PojoDescriptor>> entry : packages.entrySet()) {
            try {
                create(env, entry.getKey(), entry.getValue());
            } catch (IOException cause) {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR, "PojoMapperFactoryGenerator -> IOException for [" + entry + "] -> [" + cause.getMessage() + "]");
            }
        }
    }



    private void create(ProcessingEnvironment env, String pack, List<PojoDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Mappers");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, PojoMapperFactoryGenerator.class.getName());

        writer.write("public final class Mappers {");
        writeNewLine(writer);

        for (PojoDescriptor desc : descriptors) {
            writeNewLine(writer);
            writer.write("    public static final ");
            writer.write(Mapper.class.getName());
            writer.write("<");
            writer.write(desc.element().toString());
            writer.write("> ");

            writer.write(Helper.toUpperCase(desc.element().getSimpleName().toString()));

            writer.write(" = new ");
            writer.write(desc.element().getSimpleName().toString());
            writer.write("Mapper();");
            writeNewLine(writer);

        }

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }
}
