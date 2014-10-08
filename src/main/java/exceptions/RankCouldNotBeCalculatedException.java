package exceptions;

public class RankCouldNotBeCalculatedException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public RankCouldNotBeCalculatedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public RankCouldNotBeCalculatedException(String message) {
		super(message);
	}
}
