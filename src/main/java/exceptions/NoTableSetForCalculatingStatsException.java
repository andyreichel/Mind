package exceptions;

public class NoTableSetForCalculatingStatsException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public NoTableSetForCalculatingStatsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoTableSetForCalculatingStatsException(String message) {
		super(message);
	}
}
