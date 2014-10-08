package exceptions;

import java.io.IOException;

public class PropertyNotFoundException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public PropertyNotFoundException(String message) {
		super(message);
	}

}
