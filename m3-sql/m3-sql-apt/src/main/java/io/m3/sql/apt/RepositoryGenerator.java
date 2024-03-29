package io.m3.sql.apt;

import static io.m3.sql.apt.Helper.extractPrimaryKeyGenerator;
import static io.m3.sql.apt.Helper.hasAutoIncrementPK;
import static io.m3.sql.apt.Helper.preparedStatementSetter;
import static io.m3.sql.apt.Helper.toUpperCase;
import static io.m3.sql.apt.Helper.writeGenerated;
import static io.m3.sql.apt.Helper.writeNewLine;
import static io.m3.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import io.m3.sql.Database;
import io.m3.sql.Repository;
import io.m3.sql.annotation.AutoIncrement;
import io.m3.sql.annotation.CreateTimestamp;
import io.m3.sql.annotation.JoinTable;
import io.m3.sql.annotation.Table;
import io.m3.sql.annotation.UpdateTimestamp;
import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.PojoDescriptor;
import io.m3.sql.apt.model.PojoPropertyDescriptor;
import io.m3.sql.domain.Page;
import io.m3.sql.domain.Pageable;
import io.m3.sql.expression.AggregateFunctions;
import io.m3.sql.expression.Expressions;
import io.m3.sql.id.NoIdentifierGenerator;
import io.m3.sql.id.SequenceGenerator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class RepositoryGenerator implements Generator {

	private final Logger logger;

	RepositoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoImplementationGenerator.class);
	}
	
    @Override
    public void generate(ProcessingEnvironment processingEnvironment, List<PojoDescriptor> descriptors, Map<String, Object> properties) {

        // generate Implementation class;
        descriptors.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("PojoImplementationGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.getPackage() + ".Abstract" + descriptor.simpleName() +"Repository");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);

        writeMethods(writer, descriptor);

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
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.IDS;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.COLUMNS;");
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

        for (PojoPropertyDescriptor bk : descriptor.businessKeys()) {
            writer.write("import static ");
            writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.");
            writer.write(toUpperCase(bk.name()));
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".eq;");
        writeNewLine(writer);
        writer.write("import static ");
        writer.write(Expressions.class.getName() + ".and;");
        writeNewLine(writer);


        writeGenerated(writer, RepositoryGenerator.class.getName());

        writer.write("public abstract class Abstract");
        writer.write(descriptor.simpleName() + "Repository");
        writer.write(" extends ");
        writer.write(Repository.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private final String findById;");
        writeNewLine(writer);

        if (descriptor.businessKeys().size() > 0) {
            writer.write("    private final String findByBusinessKey;");
            writeNewLine(writer);
        }

        if (descriptor.getTable() != null && !descriptor.getTable().immutable()) {
            writer.write("    private final String findByIdForUpdate;");
            writeNewLine(writer);
            writer.write("    private final String insert;");
            writeNewLine(writer);
            writer.write("    private final String update;");
            writeNewLine(writer);
        }
        
        JoinTable joinTable = descriptor.element().getAnnotation(JoinTable.class);
        if (joinTable != null) {
            writer.write("    private final String insert;");
            writeNewLine(writer);
        }


        for (PojoPropertyDescriptor id : descriptor.ids()) {
            Class<?> identifier = extractPrimaryKeyGenerator(id.getter());
            
            if (identifier == null ) {
            	// case of JointTable
            	continue;
            }
            
            if (identifier.isAssignableFrom(NoIdentifierGenerator.class)) {
                continue;
            }
            if (SequenceGenerator.class.isAssignableFrom(identifier)) {
                writeNewLine(writer);
                writer.write("    private final ");
                writer.write(identifier.getName());
                writer.write(" idGenerator = new ");
                writer.write(identifier.getName());
                writer.write("(database(), ");
                writer.write(descriptor.fullyQualidiedClassName() + "Descriptor.SEQUENCE");
                writer.write(");");
                writeNewLine(writer);
            }
        }

        writeNewLine(writer);

    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writer.write("    protected Abstract");
        writer.write(descriptor.simpleName() + "Repository(");
        writer.write(Database.class.getName());
        writer.write(" database){");
        writeNewLine(writer);
        writer.write("        super(database);");
        writeNewLine(writer);

        generateFindById(writer, descriptor);
        generateFindByIdForUpdate(writer, descriptor);
        generateFindByBusinessKey(writer, descriptor);
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
            writer.write(")");
        } else {
        	StringBuilder builder = new StringBuilder();
        	builder.append("and(");
        	for (PojoPropertyDescriptor id : descriptor.ids()) {
        		builder.append("eq(");
        		builder.append(toUpperCase(id.name()));
        		builder.append("),");
        	}
        	builder.deleteCharAt(builder.length() - 1);
        	builder.append(")");
        	writer.write(builder.toString());
        }
        writer.write(").build();");
        writeNewLine(writer);

    }

    private static void generateFindByIdForUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }
    	
    	if (descriptor.getJoinTable() != null) {
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

    private static void generateFindByBusinessKey(Writer writer, PojoDescriptor descriptor) throws IOException {

        List<PojoPropertyDescriptor> ppds = descriptor.businessKeys();

        if (ppds.size() == 0) {
            return;
        }

        writer.write("        this.findByBusinessKey = select(ALL).from(TABLE).where(");

        if (ppds.size() > 1) {
            writer.write("and(");
        }
        for (int i = 0; i < ppds.size(); i++) {
            writer.write("eq(");
            writer.write(Helper.toUpperCase(ppds.get(i).name()));
            writer.write(')');
            if (i + 1 < ppds.size()) {
                writer.write(", ");
            }
        }
        if (ppds.size() > 1) {
            writer.write(')');
        }
        writer.write(").build();");
        writeNewLine(writer);
    }

    private static void generateInsert(Writer writer, PojoDescriptor descriptor) throws IOException {
        
    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }

        if (hasAutoIncrementPK(descriptor)) {
            writer.write("        this.insert = insert(TABLE, ");
            writer.write(ImmutableList.class.getName());
            writer.write(".of(), COLUMNS).build();");
        } else {
            writer.write("        this.insert = insert(TABLE, IDS, COLUMNS).build();");
        }
        writeNewLine(writer);

    }

    private static void generateUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getTable() != null && descriptor.getTable().immutable()) {
            return;
        }
    	
    	if (descriptor.getJoinTable() != null) {
    		return;
    	}

        writer.write("        this.update = update(TABLE, COLUMNS, IDS).build();");
        writeNewLine(writer);

    }


    private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {
        generateMethodFindById(writer, descriptor);
        generateMethodFindByIdForUpdate(writer, descriptor);
        generateMethodInsert(writer, descriptor);
        generateMethodUpdate(writer, descriptor);
       // generateMethodBatch(writer, descriptor);
        generateMethodFindByBusinessKey(writer, descriptor);
        generateMethodPage(writer, descriptor);
    }


    private static void generateMethodFindById(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findById(");

        StringBuilder builder = new StringBuilder();
        StringBuilder ps = new StringBuilder();
        int i = 1;
        for (PojoPropertyDescriptor id : descriptor.ids()) {
        	builder.append(id.getter().getReturnType().toString());
        	builder.append(' ');
        	builder.append(id.name());
        	builder.append(',');
        	
        	ps.append("           ");
        	ps.append("ps.");
        	ps.append(preparedStatementSetter(id.getter().getReturnType().toString()));
        	ps.append("(");
        	ps.append(i++);
        	ps.append(", ");
        	ps.append(id.name());
        	ps.append(");\n");
        }
        builder.deleteCharAt(builder.length() - 1);

        writer.write(builder.toString());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        return executeSelect(this.findById, ps -> {");

        writeNewLine(writer);
        writer.write(ps.toString());
        writer.write("        }");
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodFindByIdForUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getJoinTable() != null) {
    		return;
    	}
    	
        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findByIdForUpdate(");

        if (descriptor.ids().size() == 1) {
            writer.write(descriptor.ids().get(0).getter().getReturnType().toString());
            writer.write(" id");
        } else {

        }

        writer.write(") {");
        writeNewLine(writer);
        writer.write("        return executeSelect(this.findByIdForUpdate, ps -> ");

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

        for (PojoPropertyDescriptor id : descriptor.ids()) {
            Class<?> identifier = extractPrimaryKeyGenerator(id.getter());
            if (identifier == null || identifier.isAssignableFrom(NoIdentifierGenerator.class)) {
                continue;
            }
            if (SequenceGenerator.class.isAssignableFrom(identifier)) {
                writeNewLine(writer);
                writer.write("        // generate identifier");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(id.setter().getSimpleName().toString());
                writer.write("(idGenerator.next());");
                writeNewLine(writer);
            }
        }

        for (PojoPropertyDescriptor property : descriptor.properties()) {
            if (property.getter().getAnnotation(CreateTimestamp.class) != null) {
                writeNewLine(writer);
                writer.write("        // set create timestamp");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(property.setter().getSimpleName().toString());
                writer.write("(new ");
                writer.write(Timestamp.class.getName());
                writer.write("(System.currentTimeMillis()));");
                writeNewLine(writer);
            }
        }

        writeNewLine(writer);
        writer.write("        // execute insert");
        writeNewLine(writer);

        if (descriptor.ids().size() == 1 && descriptor.ids().get(0).getter().getAnnotation(AutoIncrement.class) != null) {
            writer.write("        executeInsertAutoIncrement(this.insert, Mappers.");
        } else {
            writer.write("        executeInsert(this.insert, Mappers.");
        }

        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void generateMethodUpdate(Writer writer, PojoDescriptor descriptor) throws IOException {

    	if (descriptor.getJoinTable() != null) {
    		return;
    	}
    	
        writeNewLine(writer);
        writer.write("    public final void update(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");

        for (PojoPropertyDescriptor property : descriptor.properties()) {
            if (property.getter().getAnnotation(UpdateTimestamp.class) != null) {
                writeNewLine(writer);
                writer.write("        // set update timestamp");
                writeNewLine(writer);
                writer.write("        pojo.");
                writer.write(property.setter().getSimpleName().toString());
                writer.write("(new ");
                writer.write(Timestamp.class.getName());
                writer.write("(System.currentTimeMillis()));");
                writeNewLine(writer);
            }
        }

        writeNewLine(writer);
        writer.write("        // execute update");
        writeNewLine(writer);
        writer.write("        executeUpdate(this.update, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

   /* private static void generateMethodBatch(Writer writer, PojoDescriptor descriptor) throws IOException {

        writeNewLine(writer);
        writer.write("    public final void batch(");
        writer.write(descriptor.element().toString());
        writer.write(" pojo) {");


        writeNewLine(writer);
        writer.write("        // add to batch");
        writeNewLine(writer);
        writer.write("        addBatch(this.insert, Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pojo);");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }*/

    private static void generateMethodFindByBusinessKey(Writer writer, PojoDescriptor descriptor) throws IOException {

        List<PojoPropertyDescriptor> businessKeys = descriptor.businessKeys();
        if (businessKeys.size() == 0) {
            return;
        }

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(descriptor.element().toString());
        writer.write(" findByBusinessKey(");

        for (int i = 0; i < businessKeys.size(); i++) {
            PojoPropertyDescriptor ppd = businessKeys.get(i);
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(' ');
            writer.write(ppd.name());
            if (i + 1 < businessKeys.size()) {
                writer.write(", ");
            }
        }
        writer.write(") {");
        writeNewLine(writer);

        writer.write("        return executeSelect(this.findByBusinessKey, ps -> ");

        if (businessKeys.size() == 1) {
            writer.write("ps.");
            writer.write(preparedStatementSetter(businessKeys.get(0).getter().getReturnType().toString()));
            writer.write("(1, ");
            writer.write(businessKeys.get(0).name());
            writer.write(")");

        } else {
            writer.write('{');
            writeNewLine(writer);
            for (int i = 0; i < businessKeys.size(); i++) {
                writer.write("            ");
                writer.write("ps.");
                writer.write(preparedStatementSetter(businessKeys.get(i).getter().getReturnType().toString()));
                writer.write('(');
                writer.write("" + (i + 1));
                writer.write(", ");
                writer.write(businessKeys.get(i).name());
                writer.write(");");
                writeNewLine(writer);
            }
            writeNewLine(writer);
            writer.write("        }");
        }

        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }


    private static void generateMethodPage(Writer writer, PojoDescriptor descriptor) throws IOException {
        
    	if (descriptor.getJoinTable() != null) {
    		return;
    	}
    	
    	Table table = descriptor.getTable();
        
        if (!table.pageable()) {
            return;
        }

        writeNewLine(writer);
        writer.write("    public final ");
        writer.write(Page.class.getName());
        writer.write("<");
        writer.write(descriptor.element().toString());
        writer.write(">");
        writer.write(" page(");
        writer.write(Pageable.class.getName());
        writer.write(" pageable) {");
        writeNewLine(writer);
        writer.write("        // create sql for count");
        writeNewLine(writer);

        writer.write("        String sqlCount = select("+AggregateFunctions.class.getName() + ".count(ID)).from(TABLE).build();");
        writeNewLine(writer);
        writer.write("        // create sql for select");
        writeNewLine(writer);
        writer.write("        String sqlPage = select(ALL).from(TABLE).offset(pageable.getPageNumber()* pageable.getPageSize()).limit(pageable.getPageSize()).build();");
        writeNewLine(writer);
        writer.write("        return executeSelectPage(sqlCount, sqlPage");
        writer.write(", Mappers.");
        writer.write(toUpperCase(descriptor.element().getSimpleName().toString()));
        writer.write(", pageable);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

}