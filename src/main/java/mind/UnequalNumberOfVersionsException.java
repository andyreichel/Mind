package mind;

public class UnequalNumberOfVersionsException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public UnequalNumberOfVersionsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UnequalNumberOfVersionsException(String message) {
		super(message);
	}

}
