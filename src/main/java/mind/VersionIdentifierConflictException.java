package mind;

public class VersionIdentifierConflictException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public VersionIdentifierConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public VersionIdentifierConflictException(String message) {
		super(message);
	}
}
