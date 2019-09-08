package io.m3.sql.apt.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;

public final class Logger {

	private final String name;
	private final Writer writer;
	
	public Logger(Class<?> clazz, Writer writer) {
		this.name = clazz.getSimpleName();
		this.writer = writer;
	}

	public void info(String msg) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append(" [INFO] [").append(this.name).append("] ").append(msg).append('\n');
		try {
			this.writer.write(builder.toString());
		} catch (IOException cause) {
			throw new IllegalStateException(cause);
		}
	}
	
	public void error(String msg) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append(" [ERROR] [").append(this.name).append("] ").append(msg).append('\n');
		try {
			this.writer.write(builder.toString());
		} catch (IOException cause) {
			throw new IllegalStateException(cause);
		}
	}

	public void error(String msg, Exception cause) {
		StringBuilder builder = new StringBuilder();
		builder.append(LocalDateTime.now().toString()).append(" [").append(this.name).append("] ").append(msg).append('\n');
		cause.printStackTrace(new PrintWriter(writer));
		try {
			this.writer.write(builder.toString());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
