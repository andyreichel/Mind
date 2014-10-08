package exceptions;

public class LenghtOfDoubleArraysDifferException extends Exception{
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public LenghtOfDoubleArraysDifferException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public LenghtOfDoubleArraysDifferException(String message) {
		super(message);
	}
}
