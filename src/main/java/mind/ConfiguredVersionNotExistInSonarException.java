package mind;

public class ConfiguredVersionNotExistInSonarException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public ConfiguredVersionNotExistInSonarException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ConfiguredVersionNotExistInSonarException(String message) {
		super(message);
	}
}
