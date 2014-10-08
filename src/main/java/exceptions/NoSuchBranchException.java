package exceptions;

import org.eclipse.jgit.api.errors.GitAPIException;


public class NoSuchBranchException extends GitAPIException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchBranchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoSuchBranchException(String message) {
		super(message);
	}
}
