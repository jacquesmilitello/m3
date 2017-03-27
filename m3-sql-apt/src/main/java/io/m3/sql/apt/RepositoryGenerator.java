package io.m3.sql.apt;

import io.m3.sql.Database;
import io.m3.sql.Repository;
import io.m3.sql.annotation.Table;
import io.m3.sql.expression.Expressions;

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
final class RepositoryGenerator implements Generator {

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
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "AbstractRepository");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);

        writeMethods(writer,descriptor);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeNewLine(writer);

        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.ALL;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.TABLE;");
        writeNewLine(writer);
        for (PojoPropertyDescriptor id : descriptor.ids()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(id.name()));
            writer.write(";");
            writeNewLine(writer);
        }
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".eq;");
        writeNewLine(writer);

        writeGenerated(writer, RepositoryGenerator.class.getName());

        writer.write("public abstract class ");
        writer.write(descriptor.simpleName() + "AbstractRepository");
        writer.write(" extends ");
        writer.write(Repository.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private final String findById;");
        writeNewLine(writer);

        if (!descriptor.element().getAnnotation(Table.class).immutable()) {
            writer.write("    private final String findByIdForUpdate;");
            writeNewLine(writer);
            writer.write("    private final String insert;");
            writeNewLine(writer);
            writer.write("    private final String update;");
            writeNewLine(writer);
        }

        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writer.write("    protected ");
        writer.write(descriptor.simpleName() + "AbstractRepository(");
        writer.write(Database.class.getName());
        writer.write(" database){");
        writeNewLine(writer);
        writer.write("        super(database);");
        writeNewLine(writer);

        generateFindById(writer, descriptor);
        generateFindByIdForUpdate(writer,descriptor);
        generateInsert(writer, descriptor);
        generateUpdate(writer, descriptor);

        writer.write("    }");
        writeNewLine(writer);
    }

    private static void generateFindById(Writer writer, PojoDescriptor descriptor) throws IOException {
        writer.write("        this.findById = select(ALL).from(TABLE).where(");
        if (descriptor.ids().size() == 1) {
            writer.write("eq(");
            writer.write(toUpperCase(descriptor.ids().get(0).name()));
            writer.write(")).build();");
        } else {
        }
        writeNewLine(writer);

    }

    private static void generateFindByIdForUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

        if (descriptor.element().getAnnotation(Table.class).immutable()) {
            return;
        }

        writer.write("        this.findByIdForUpdate = select(ALL).from(TABLE).where(");
        if (descriptor.ids().size() == 1) {
            writer.write("eq(");
            writer.write(toUpperCase(descriptor.ids().get(0).name()));
            writer.write(")).forUpdate().build();");
        } else {
        }
        writeNewLine(writer);

    }

    private static void generateInsert(Writer writer, PojoDescriptor descriptor) throws IOException {
        if (descriptor.element().getAnnotation(Table.class).immutable()) {
            return;
        }
        writer.write("        this.insert = insert(TABLE,ALL).build();");
        writeNewLine(writer);

    }

    private static void generateUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

        if (descriptor.element().getAnnotation(Table.class).immutable()) {
            return;
        }

        writer.write("        this.update = update(TABLE,ALL).build();");
        writeNewLine(writer);

    }


    private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {
        generateMethodFindById(writer, descriptor);
        generateMethodInsert(writer, descriptor);
        generateMethodUpdate(writer, descriptor);
        generateMethodBatch(writer, descriptor);
    }

    private static void generateMethodFindById(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findById(");

        if (descriptor.ids().size() == 1) {
            writer.write(descriptor.ids().get(0).getter().getReturnType().toString());
            writer.write(" id");
        } else {

        }

        writer.write(") {");
        writeNewLine(writer);
        writer.write("         return executeSelect(this.findById, ps -> ");

        if (descriptor.ids().size() == 1) {
            PojoPropertyDescriptor ppd = descriptor.ids().get(0);
            writer.write("ps.");
            writer.write(preparedStatementSetter(ppd.getter().getReturnType().toString()));
            writer.write("(1, id)");
        } else {

        }
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodInsert(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final void insert(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");


        writeNewLine(writer);
        writer.write("         // execute insert");
        writeNewLine(writer);
        writer.write("         executeInsert(this.insert, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final void update(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");


        writeNewLine(writer);
        writer.write("         // execute update");
        writeNewLine(writer);
        writer.write("         executeUpdate(this.update, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodBatch(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final void batch(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");


        writeNewLine(writer);
        writer.write("         // add to batch");
        writeNewLine(writer);
        writer.write("         addBatch(this.insert, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }
}