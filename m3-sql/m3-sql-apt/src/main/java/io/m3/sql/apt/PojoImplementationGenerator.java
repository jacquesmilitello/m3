package io.m3.sql.apt;

import static io.m3.sql.apt.Helper.isPrimitiveType;
import static io.m3.sql.apt.Helper.writeGenerated;
import static io.m3.sql.apt.Helper.writeNewLine;
import static io.m3.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.PojoDescriptor;
import io.m3.sql.apt.model.PojoPropertyDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoImplementationGenerator implements Generator {

	private final Logger logger;
	
	PojoImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoImplementationGenerator.class);
	}
	
    @Override
    public void generate(ProcessingEnvironment processingEnvironment, List<PojoDescriptor> descriptors, Map<String, Object> properties) {
        // generate Implementation class;
        descriptors.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
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
        writeEquals(writer, descriptor);

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
            writer.write(ppd.variable());
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
        writer.write(ppd.variable());
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
        writer.write(ppd.variable());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this.");
        writer.write(ppd.variable());
        writer.write(" = ");
        writer.write(ppd.variable());
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
            writer.write("\", this.");
            writer.write(ppd.variable());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeEquals(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public boolean equals(Object object) {");
        writeNewLine(writer);

        writer.write("        if (object == this) {");
        writeNewLine(writer);
        writer.write("            return true;");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);

        writer.write("        if ((object == null) || (!(object instanceof ");
        writer.write(descriptor.element().toString());
        writer.write("))) {");
        writeNewLine(writer);
        writer.write("            return false;");
        writeNewLine(writer);
        writer.write("        }");
        writeNewLine(writer);


        writer.write("        ");
        writer.write(descriptor.element().toString());
        writer.write(" other = (");
        writer.write(descriptor.element().toString());
        writer.write(") object;");
        writeNewLine(writer);

        List<PojoPropertyDescriptor> businessKeys  = descriptor.businessKeys();

        if (businessKeys.size() == 0) {
            writer.write("        // no business key -> use the primary keys");
            writeNewLine(writer);

            writer.write("        return ");
            int number = descriptor.ids().size();
            for (int i = 0 ; i < number ; i++) {
                writeEqualsPojoPropertyDescriptor(writer, descriptor.ids().get(i));
                if (i + 1 < number) {
                    writer.write(" && ");
                    writeNewLine(writer);
                    writer.write("           ");
                }
            }

            writer.write(";");
            writeNewLine(writer);
        } else {
            int number = businessKeys.size();

            writer.write("        // " +number + " business key" + ((number >1) ? "s" : "") + " on propert"+ ((number >1) ? "ies" : "y") + " : ");
            for (int i = 0 ; i < number ; i++) {
                writer.write(businessKeys.get(i).name());
                if (i + 1 < number) {
                    writer.write(", ");
                }
            }
            writeNewLine(writer);
            writer.write("        return ");
            for (int i = 0 ; i < number ; i++) {
                writeEqualsPojoPropertyDescriptor(writer, businessKeys.get(i));
                if (i + 1 < number) {
                    writer.write(" && ");
                    writeNewLine(writer);
                    writer.write("           ");
                }
            }
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);

    }


    private static void writeEqualsPojoPropertyDescriptor(Writer writer, PojoPropertyDescriptor ppd) throws IOException {
        writer.write(ppd.name());

        if (isPrimitiveType(ppd.getter().getReturnType().toString())) {
            writer.write(" == other.");
            writer.write(ppd.getter().toString());
        } else {
            writer.write(".equals(");
            writer.write(ppd.getter().toString());
            writer.write(")");
        }
    }
}