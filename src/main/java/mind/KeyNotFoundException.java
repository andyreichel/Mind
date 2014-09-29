package mind;

public class KeyNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public KeyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public KeyNotFoundException(String message) {
		super(message);
	}

}
