package io.m3.sql.apt.flyway;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.Column;
import io.m3.sql.annotation.Database;
import io.m3.sql.annotation.Flyway;
import io.m3.sql.annotation.PrimaryKey;
import io.m3.sql.annotation.Table;
import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.GlobalConfigurationDescriptor;
import io.m3.sql.apt.model.PojoDescriptor;
import io.m3.sql.apt.model.PojoPropertyDescriptor;
import io.m3.sql.apt.util.Tuple;

public class FlywayGenerator {

	private final Logger logger;

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

		
	}


	private void generate(ProcessingEnvironment env, PojoDescriptor descriptor, Flyway flyway, FlywayDialect fd) throws IOException {

		List<Column> businessKeys = new ArrayList<>();
		
		FileObject object = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "db.migration",
				"V" + flyway.version() + "__" + flyway.description() + ".sql");

		Writer writer = object.openWriter();

		Table table = descriptor.element().getAnnotation(Table.class);

		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ");
		builder.append(table.value());
		builder.append(" (");
		
		StringBuilder primaryKey = new StringBuilder();
		primaryKey.append("PRIMARY KEY (");

		for (PojoPropertyDescriptor id : descriptor.ids()) {
			builder.append("\n   ");
			PrimaryKey anno = id.getter().getAnnotation(PrimaryKey.class);
			builder.append(anno.value());
			builder.append("   ");
			builder.append(fd.toSqlType(id.getter().getReturnType().toString(), id.getter().getAnnotation(Column.class)));
			builder.append(",");
			
			primaryKey.append(anno.value()).append("),");
		}
		primaryKey.deleteCharAt(primaryKey.length()-1).append(')');

		for (PojoPropertyDescriptor col : descriptor.properties()) {
			builder.append("\n   ");
			Column anno = col.getter().getAnnotation(Column.class);
			builder.append(anno.value());
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
		
		if (businessKeys.size() > 0) {
			generateUniqueIndex(table, businessKeys, writer);
		}
		
		
		writer.close();
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
		builder.append(");");
		
		writer.append(builder.toString());
	}

	
}
