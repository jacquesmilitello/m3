package io.m3.sql.apt;

import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;
import io.m3.sql.desc.SqlColumn;
import io.m3.sql.desc.SqlSingleColumn;
import io.m3.util.ImmutableList;

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
final class PojoDescriptorGenerator implements Generator {

    /**
     * key for properties (holder of all aliases generated).
     */
    public static final String KEY = "pojo.descriptor";

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, List<PojoDescriptor> descriptors, Map<String, Object> properties) {

        // generate Implementation class;
        descriptors.forEach(t -> {
            try {
                generate(processingEnvironment, t, properties);
            } catch (IOException cause) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "PojoImplementationGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]");
            }
        });

    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor, Map<String, Object> properties) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Descriptor");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeTable(writer, descriptor, properties);
        writePrimaryKeys(writer, descriptor);
        writeProperties(writer, descriptor);
        writeAllColumns(writer, descriptor);

        writer.write("}");
        writer.close();
    }



    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, PojoDescriptorGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Descriptor");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private ");
        writer.write(descriptor.simpleName() + "Descriptor");
        writer.write(" () {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeTable(Writer writer, PojoDescriptor descriptor, Map<String, Object> properties) throws IOException {
        writeNewLine(writer);
        writer.write("    // SQL TABLE DESCRIPTOR");
        writeNewLine(writer);
        writer.write("    public static final ");
        writer.write(io.m3.sql.desc.SqlTable.class.getName());
        writer.write(" TABLE = new ");
        writer.write(io.m3.sql.desc.SqlTable.class.getName());
        writer.write("(\"");
        writer.write(descriptor.element().getAnnotation(Table.class).value());
        writer.write("\", \"");
        writer.write(generateAlias(properties));
        writer.write("\");");
        writeNewLine(writer);
    }

    private static void writePrimaryKeys(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // SQL PRIMARY KEY");
        writeNewLine(writer);

        if (descriptor.ids().size() == 1) {
            writer.write("    public static final ");
            writer.write(io.m3.sql.desc.SqlPrimaryKey.class.getName());
            writer.write(" ");
            writer.write(toUpperCase(descriptor.ids().get(0).name()));
            writer.write(" = new ");
            writer.write(io.m3.sql.desc.SqlPrimaryKey.class.getName());
            writer.write("(TABLE, \"");
            writer.write(descriptor.ids().get(0).getter().getAnnotation(PrimaryKey.class).value());
            writer.write("\");");
            writeNewLine(writer);
        } else {
            throw new UnsupportedOperationException("not yet supported");
        }
    }

    private static void writeProperties(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // SQL PROPERTIES");
        writeNewLine(writer);

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {

            Column column = ppd.getter().getAnnotation(Column.class);

            writer.write("    public static final ");
            writer.write(SqlSingleColumn.class.getName());
            writer.write(" ");
            writer.write(toUpperCase(ppd.name()));
            writer.write(" = new ");
            writer.write(SqlSingleColumn.class.getName());
            writer.write("(TABLE, \"");
            writer.write(column.value());
            writer.write("\", ");
            writer.write("" + column.nullable());
            writer.write(", ");
            writer.write("" + column.insertable());
            writer.write(", ");
            writer.write("" + column.updatable());
            writer.write(");");
            writeNewLine(writer);
        }

    }

    private static void writeAllColumns(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    // ALL COLUMNS");
        writeNewLine(writer);

        writer.write("    public static final ");
        writer.write(ImmutableList.class.getName());
        writer.write("<");
        writer.write(SqlColumn.class.getName());
        writer.write("> ALL = ");
        writer.write(ImmutableList.class.getName());
        writer.write(".of(");

        StringBuilder builder = new StringBuilder();


        for (PojoPropertyDescriptor id : descriptor.ids()) {
//            writer.write("        ");
//            writer.write(toUpperCase(id.name()));
//            writer.write(',');
//            writeNewLine(writer);
            writeNewLine(builder);
            builder.append("        ");
            builder.append(toUpperCase(id.name()));
            builder.append(',');

        }

        for (PojoPropertyDescriptor id : descriptor.properties()) {
            writeNewLine(builder);
            builder.append("        ");
            builder.append(toUpperCase(id.name()));
            builder.append(',');

        }

        builder.deleteCharAt(builder.length()-1);

        writer.write(builder.toString());
        writer.write(");");
        writeNewLine(writer);

    }

    private static String generateAlias(Map<String, Object> properties) {

        String current = (String) properties.get(KEY);

        if (current == null) {
            properties.put(KEY, "a");
            return "a";
        }

        if ("z".equals(current)) {
            properties.put(KEY, "aa");
            return "aa";
        }

        if ("zz".equals(current)) {
            properties.put(KEY, "aaa");
            return "aaa";
        }

        if ("zzz".equals(current)) {
            properties.put(KEY, "aaaa");
            return "aaaa";
        }

        StringBuilder builder = new StringBuilder();

        if (current.length() > 1) {
            builder.append(current.substring(0, current.length()-2));
        }
        builder.append((char)(current.charAt(current.length()-1)+1));

        return builder.toString();
    }
}
