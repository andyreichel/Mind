package exceptions;

public class AverageCouldNotBeCalculatedException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public AverageCouldNotBeCalculatedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AverageCouldNotBeCalculatedException(String message) {
		super(message);
	}
}
