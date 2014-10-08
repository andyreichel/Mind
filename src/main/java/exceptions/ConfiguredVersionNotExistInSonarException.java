package exceptions;

/**
 * Exception that is thrown if a sonar.versionTags in the mind.properties file is not findable in the 
 * configured sonar system
 * 
 *
 */
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
