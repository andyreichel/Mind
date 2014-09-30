package mind;

public class NoSuchSonarVersionException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchSonarVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoSuchSonarVersionException(String message) {
		super(message);
	}
}
