package io.m3.sql.apt;

import io.m3.sql.Module;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static io.m3.sql.apt.Helper.*;
import static java.lang.Character.toUpperCase;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ModuleGenerator implements Generator {

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

        String classname = className(pack);

        JavaFileObject object = env.getFiler().createSourceFile(classname);
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, ModuleGenerator.class.getName());

        writer.write("public final class " + classname + " extends ");
        writer.write(Module.class.getName());
        writer.write(" { ");
        writeNewLine(writer);

        writeConstructor(writer, env, classname, descriptors);

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }

    private static void writeConstructor(Writer writer, ProcessingEnvironment env, String classname, List<PojoDescriptor> descriptors) throws IOException {

        writeNewLine(writer);
        writer.write("     public " + classname + "(String name, String catalog) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog");

        for (PojoDescriptor desc : descriptors) {
            writer.write(", ");
            writer.write(desc.simpleName() + "Descriptor.INSTANCE");
        }
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");

    }

    private static String className(String pack) {

        String[] parts = pack.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        builder.append("Module");
        return builder.toString();
    }
}
