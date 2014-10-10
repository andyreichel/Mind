package exceptions;

public class PValueCouldNotBeCalculatedException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public PValueCouldNotBeCalculatedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public PValueCouldNotBeCalculatedException(String message) {
		super(message);
	}
}
