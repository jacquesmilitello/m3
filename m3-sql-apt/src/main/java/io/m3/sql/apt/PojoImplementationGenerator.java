package io.m3.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static io.m3.sql.apt.Helper.*;
import static io.m3.sql.apt.Helper.writeNewLine;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoImplementationGenerator implements Generator {

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, List<PojoDescriptor> descriptors, Map<String, Object> properties) {

        // generate Implementation class;
        descriptors.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (IOException cause) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "PojoImplementationGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]");
            }
        });


    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Impl");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);
        writeToStringBuilder(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,PojoImplementationGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write(" implements ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(){}");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.ids());
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<PojoPropertyDescriptor> descriptors) throws IOException {
        for (PojoPropertyDescriptor ppd : descriptors) {
            writer.write("    private ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.name());
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.ids());
        writeMethods(writer, descriptor.properties());
    }

    private static void writeMethods(Writer writer, List<PojoPropertyDescriptor> descriptors) throws IOException {
        for (PojoPropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
            writeSetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, PojoPropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        return this.");
        writer.write(ppd.name());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeSetter(Writer writer, PojoPropertyDescriptor ppd) throws IOException {

        if (ppd.setter() == null) {
            return;
        }

        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ppd.setter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.setter().getSimpleName().toString());
        writer.write("(");
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(" ");
        writer.write(ppd.name());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this.");
        writer.write(ppd.name());
        writer.write(" = ");
        writer.write(ppd.name());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void writeToStringBuilder(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        io.m3.util.ToStringBuilder builder = new io.m3.util.ToStringBuilder(this);");
        writeNewLine(writer);
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\",");
            writer.write(ppd.name());
            writer.write(");");
            writeNewLine(writer);
        }
        for (PojoPropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\",");
            writer.write(ppd.name());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


}