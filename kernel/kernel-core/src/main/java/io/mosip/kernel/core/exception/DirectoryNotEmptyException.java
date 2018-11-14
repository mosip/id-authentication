package io.mosip.kernel.core.exception;

/**
 * Exception to be thrown when a directory exist which is not Empty
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class DirectoryNotEmptyException extends BaseUncheckedException {

	private static final long serialVersionUID = -381238520404127950L;


	public DirectoryNotEmptyException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

}
