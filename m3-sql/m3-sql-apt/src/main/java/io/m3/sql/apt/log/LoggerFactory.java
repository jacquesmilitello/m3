package io.m3.sql.apt.log;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public final class LoggerFactory {

	private static final LoggerFactory INSTANCE = new LoggerFactory();

	private Writer writer;

	private LoggerFactory() {
	}

	public static LoggerFactory getInstance() {
		return INSTANCE;
	}

	public void init(ProcessingEnvironment env) {
		FileObject object;
		try {
			object = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "io.m3.report","Output.txt");
			writer = object.openWriter();
		} catch (IOException cause) {
			throw new IllegalStateException(cause);
		}

	}

	public void close() {
		if (this.writer != null) {
			try {
				this.writer.close();
			} catch (IOException cause) {
				throw new IllegalStateException(cause);
			}
		}
	}

	public Logger getLogger(Class<?> clazz) {
		return new Logger(clazz, this.writer);
	}


}
