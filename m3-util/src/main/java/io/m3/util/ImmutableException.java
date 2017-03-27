package io.m3.util;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ImmutableException extends UnsupportedOperationException{

	/**
	 * Default serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public ImmutableException(String method, Object ...objects) {
		super(buildMessage(method, objects));
	}

	private static String buildMessage(String method, Object ... objects) {
		StringBuilder builder = new StringBuilder();
		builder.append("Method ");
		builder.append(method);
		builder.append(" not allowed for an immutable");
		if (objects != null && objects.length > 0) {
			builder.append(", params {");
			for (Object o : objects) {
				builder.append('[').append(o).append("],");
			}
			builder.append("}");
		}
		return builder.toString();
	}

}