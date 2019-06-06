package io.m3.sql.apt;

import io.m3.sql.Dialect;
import io.m3.sql.annotation.AutoIncrement;
import io.m3.sql.annotation.Column;
import io.m3.sql.jdbc.Mapper;
import io.m3.sql.jdbc.MapperWithAutoIncrement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.m3.sql.apt.Helper.hasAutoIncrementPK;
import static io.m3.sql.apt.Helper.nullableType;
import static io.m3.sql.apt.Helper.preparedStatementGetter;
import static io.m3.sql.apt.Helper.preparedStatementSetter;
import static io.m3.sql.apt.Helper.writeGenerated;
import static io.m3.sql.apt.Helper.writeNewLine;
import static io.m3.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MapperGenerator implements Generator {

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, List<PojoDescriptor> descriptors, Map<String, Object> properties) {

        // generate Implementation class;
        descriptors.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, "MapperGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]");
            }
        });


    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Mapper");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeMap(writer, descriptor);
        writeInsert(writer, descriptor, env);
        writeUpdate(writer, descriptor);

        writeSetId(writer, descriptor);


        writeNewLine(writer);
        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writeGenerated(writer, MapperGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Mapper");
        writer.write(" implements ");

        if (Helper.hasAutoIncrementPK(descriptor)) {
            writer.write(MapperWithAutoIncrement.class.getName());
        } else {
            writer.write(Mapper.class.getName());
        }

        writer.write("<");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(">");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Mapper() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeMap(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" map(");
        writer.write(Dialect.class.getName());
        writer.write(" dialect, ");
        writer.write(ResultSet.class.getName());
        writer.write(" rs) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");

        writeNewLine(writer);
        writer.write("        ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo = new ");
        writer.write(descriptor.element().getSimpleName().toString() + "Impl();");
        writeNewLine(writer);

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            writer.write("        pojo.");
            writer.write(ppd.setter().getSimpleName().toString());
            writer.write("(rs.");
            writer.write(preparedStatementGetter(ppd.getter().getReturnType().toString()));
            writer.write("(");
            writer.write("" + index++);
            writer.write("));");
            writeNewLine(writer);
        }

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {

            if (Helper.isArray(ppd.getter().getReturnType().toString())) {
                writer.write("        java.sql.Array array" + index + " = rs."
                        + preparedStatementGetter(ppd.getter().getReturnType().toString()) + "(" + index + ");");
                writeNewLine(writer);
                writer.write("        pojo." + ppd.setter().getSimpleName().toString() + "(array" + index
                        + " == null ? null : (" + ppd.getter().getReturnType().toString() + ")array" + index
                        + ".getArray());");
                writeNewLine(writer);
                index++;

            } else if (ppd.getter().getReturnType().toString().equals("java.sql.Blob")) {

                writer.write("        pojo.");
                writer.write(ppd.setter().getSimpleName().toString());
                writer.write("(");

                String method = Helper.isDialectType(ppd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write("));");
                } else {
                    writer.write("new javax.sql.rowset.serial.SerialBlob(rs.");
                    writer.write(preparedStatementGetter(ppd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write(")));");
                }
                writeNewLine(writer);
                if (ppd.getter().getAnnotation(Column.class).nullable()
                        && isLinkToPrimitive(ppd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    writeNewLine(writer);
                    writer.write("            pojo.");
                    writer.write(ppd.setter().getSimpleName().toString());
                    writer.write("(null);");
                    writeNewLine(writer);
                    writer.write("        }");
                    writeNewLine(writer);
                }
            } else {
                writer.write("        pojo.");
                writer.write(ppd.setter().getSimpleName().toString());
                writer.write("(");

                String method = Helper.isDialectType(ppd.getter().getReturnType().toString());
                if (method != null) {
                    writer.write("dialect.");
                    writer.write(method);
                    writer.write("(rs,");
                    writer.write("" + index++);
                    writer.write("));");
                } else {
                    writer.write("rs.");
                    writer.write(preparedStatementGetter(ppd.getter().getReturnType().toString()));
                    writer.write("(");
                    writer.write("" + index++);
                    writer.write("));");
                }
                writeNewLine(writer);
                if (ppd.getter().getAnnotation(Column.class).nullable()
                        && isLinkToPrimitive(ppd.getter().getReturnType().toString())) {
                    writer.write("        if (rs.wasNull()) {");
                    writeNewLine(writer);
                    writer.write("            pojo.");
                    writer.write(ppd.setter().getSimpleName().toString());
                    writer.write("(null);");
                    writeNewLine(writer);
                    writer.write("        }");
                    writeNewLine(writer);
                }
            }
        }

        writer.write("        return pojo;");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void writeInsert(Writer writer, PojoDescriptor descriptor, ProcessingEnvironment env) throws IOException {

        writeNewLine(writer);
        writer.write("    public void insert(");
        writer.write(PreparedStatement.class.getName());
        writer.write(" ps, ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");
        writeNewLine(writer);

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            if (ppd.getter().getAnnotation(AutoIncrement.class) != null) {
                writer.write("        // Primary Key [");
                writer.write(ppd.name());
                writer.write("] is auto increment -> skip");
                writeNewLine(writer);
                continue;
            }
            writePsProperty(writer, ppd, index++);
            writeNewLine(writer);
        }

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {
            if (ppd.getter().getAnnotation(Column.class).insertable()) {
                writePsProperty(writer, ppd, index++);
                writeNewLine(writer);
            }
        }

        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public void update(");
        writer.write(PreparedStatement.class.getName());
        writer.write(" ps, ");
        writer.write(descriptor.element().getSimpleName().toString());
        writer.write(" pojo) throws ");
        writer.write(SQLException.class.getName());
        writer.write(" {");
        writeNewLine(writer);

        int index = 1;

//        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
//                writePsProperty(writer, ppd, index++);
//                writeNewLine(writer);
//        }

        for (PojoPropertyDescriptor ppd : descriptor.properties()) {
            if (ppd.getter().getAnnotation(Column.class).updatable()) {
                writePsProperty(writer, ppd, index++);
                writeNewLine(writer);
            }
        }

        // for the where
        writer.write("        //set primary key");
        writeNewLine(writer);
        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
            writePsProperty(writer, ppd, index++);
            writeNewLine(writer);
        }

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writePsProperty(Writer writer, PojoPropertyDescriptor ppd, int index) throws IOException {

        Column column = ppd.getter().getAnnotation(Column.class);

        if (column != null && column.nullable()) {
            writer.write("        if (pojo.");
            writer.write(ppd.getter().toString());
            writer.write(" != null) {");
            writeNewLine(writer);
            writer.write("    ");
        }

        writer.write("        ps.");
        writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
        writer.write("(");
        writer.write("" + index);
        writer.write(", ");
        writer.write(" pojo.");
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("());");

        if (column != null && column.nullable()) {
            writeNewLine(writer);
            writer.write("        } else {");
            writeNewLine(writer);
            writer.write("            ps.setNull(");
            writer.write("" + index);
            writer.write(", ");
            writer.write(nullableType(ppd.getter().getReturnType().toString()));
            writer.write(");");
            writeNewLine(writer);
            writer.write("        }");
        }
    }


    private static void writeSetId(Writer writer, PojoDescriptor descriptor) throws IOException {

        if (!hasAutoIncrementPK(descriptor)) {
            return;
        }

        int index = 1;

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {

            if (ppd.getter().getAnnotation(AutoIncrement.class) == null) {
                continue;
            }

            writer.write("    public void setId(");
            writer.write(descriptor.element().getSimpleName().toString());
            writer.write(" pojo, ");
            writer.write(ResultSet.class.getName());
            writer.write(" rs) throws ");
            writer.write(SQLException.class.getName());
            writer.write(" {");
            writeNewLine(writer);
            writer.write("        pojo.");
            writer.write(ppd.setter().getSimpleName().toString());
            writer.write("(rs.");
            writer.write(preparedStatementGetter(ppd.getter().getReturnType().toString()));
            writer.write("(" + index++ + "));");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);

    }


    private static boolean isLinkToPrimitive(String type) {

        return ("java.lang.Integer".equals(type) ||
                "java.lang.Long".equals(type) ||
                "java.lang.Short".equals(type) ||
                "java.lang.Byte".equals(type) ||
                "java.lang.Double".equals(type) ||
                "java.lang.Float".equals(type));

    }


}
