package io.m3.sql.apt.flyway;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.m3.sql.annotation.AutoIncrement;
import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.Database;
import io.m3.sql.annotation.Flyway;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Sequence;
import io.m3.sql.annotation.Table;
import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.GlobalConfigurationDescriptor;
import io.m3.sql.apt.model.PojoDescriptor;
import io.m3.sql.apt.model.PojoPropertyDescriptor;
import io.m3.sql.apt.util.Tuple;

public class FlywayGenerator {

	private final Logger logger;
	
	private Map<String, Tuple<FileObject,Writer>> holders = new HashMap<>();

	public FlywayGenerator() {
		logger = LoggerFactory.getInstance().getLogger(FlywayGenerator.class);
	}

	public void generate(ProcessingEnvironment env, List<GlobalConfigurationDescriptor> configs) {
		for (GlobalConfigurationDescriptor gcd : configs) {
			generate(env, gcd);
		}
	}
	
	private void generate(ProcessingEnvironment env, GlobalConfigurationDescriptor gcd) {

		logger.info("Generate GlobalConfiguration for gcd");
		
		gcd.getDescriptors().forEach(pojo -> {
			
			for (Database db : gcd.getGlobalConfiguration().flywayConfiguration().database()) {
				
				logger.info("Generate for DB [" + db + "]");
				
				Flyway flyway = pojo.element().getAnnotation(Table.class).flyway();
				
				if (flyway.version().trim().length() > 0) {
					FlywayDialect fd = FlywayDialects.get(db);
					try {
						generate(env, pojo, flyway, fd);
					} catch (IOException cause) {
						logger.error("Failed to generate [" + flyway + "] -> " + db, cause);
					}
				}
				
			}
		});

		holders.forEach((filename, tuple) -> {
			try {
				tuple.getY().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}


	private void generate(ProcessingEnvironment env, PojoDescriptor descriptor, Flyway flyway, FlywayDialect fd) throws IOException {

		List<Column> businessKeys = new ArrayList<>();
		
		String filename = "V" + flyway.version() + "__" + flyway.description() + ".sql";
		
		Tuple<FileObject, Writer> tuple = holders.get(filename);
		
		if (tuple == null) {
			FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "db.migration", filename);	
			Writer writer = object.openWriter();
			tuple = new Tuple<>(object, writer);
			this.holders.put(filename, tuple);
		}
		
		
		Writer writer = tuple.getY();
		Table table = descriptor.element().getAnnotation(Table.class);

		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ");
		builder.append(fd.wrap(table.value()));
		builder.append(" (");
		
		StringBuilder primaryKey = new StringBuilder();
		primaryKey.append("PRIMARY KEY (");
		String sequence = null;
		
		for (PojoPropertyDescriptor id : descriptor.ids()) {
			builder.append("\n   ");
			PrimaryKey anno = id.getter().getAnnotation(PrimaryKey.class);
			builder.append(fd.wrap(anno.value()));
			builder.append("   ");
			
			AutoIncrement autoIncrement = id.getter().getAnnotation(AutoIncrement.class);
			if (autoIncrement != null) {
				builder.append(fd.autoIncrementType(id.getter().getReturnType().toString(), id.getter().getAnnotation(Column.class)));
			} else {
				builder.append(fd.toSqlType(id.getter().getReturnType().toString(), id.getter().getAnnotation(Column.class)));
			}
			builder.append(",");
			
			primaryKey.append(fd.wrap(anno.value())).append("),");
			
			if (id.getter().getAnnotation(Sequence.class) != null) {
				sequence = generateSequence(id.getter().getAnnotation(Sequence.class), fd);
				logger.info("generate sequence " + id.getter().getAnnotation(Sequence.class));
			}
			
		}
		primaryKey.deleteCharAt(primaryKey.length()-1).append(')');

		for (PojoPropertyDescriptor col : descriptor.properties()) {
			builder.append("\n   ");
			Column anno = col.getter().getAnnotation(Column.class);
			builder.append(fd.wrap(anno.value()));
			for (int i = anno.value().length() ; i < 24 ; i++) {
				builder.append(' ');	
			}
			String type = fd.toSqlType(col.getter().getReturnType().toString(), anno);
			builder.append(type);
			for (int i = type.length() ; i < 16 ; i++) {
				builder.append(' ');	
			}
			if (!anno.nullable()) {
				builder.append(" NOT NULL");
			}
			
			builder.append(",");
			
			BusinessKey bk = col.getter().getAnnotation(BusinessKey.class);
			if (bk != null) {
				businessKeys.add(anno);
			}
		}

		builder.append("\n   ");
		builder.append(primaryKey);
		
		builder.deleteCharAt(builder.length() - 1);
		builder.append("\n);\n");
		
		writer.append(builder.toString());
		
		if (sequence != null) {
			writer.append("\n\n");
			writer.append(sequence);
			writer.append("\n\n");
		}
		
		if (businessKeys.size() > 0) {
			generateUniqueIndex(table, businessKeys, writer);
		}
		
		
	}

	private String generateSequence(Sequence sequence, FlywayDialect fd) {
		return "CREATE SEQUENCE " + fd.wrap(sequence.value())  + ";";
		
	}

	private void generateUniqueIndex(Table table, List<Column> businessKeys, Writer writer) throws IOException {
	
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE INDEX ");
		builder.append(table.value());
		builder.append("_bk");
		builder.append(" ON ");
		builder.append(table.value());
		builder.append("(");
		businessKeys.forEach(bk -> {
			builder.append(bk.value()).append(',');
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(");\n");
		
		writer.append(builder.toString());
	}

	
}
