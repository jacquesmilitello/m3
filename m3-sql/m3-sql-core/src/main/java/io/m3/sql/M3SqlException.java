package io.m3.sql;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class M3SqlException extends RuntimeException {

    /**
	 *  default serial UID.
	 */
	private static final long serialVersionUID = -3772625520039435049L;

	public M3SqlException(String message) {
        super(message);
    }

    public M3SqlException(Exception exception) {
        super(exception);
    }

    public M3SqlException(String message, Exception exception) {
        super(message, exception);
    }

}