package io.mosip.preregistration.documents.exception;



import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * FileNotFoundException occurs when requested packet is not present
 * in DFS
 */
public class FileNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public FileNotFoundException() {
		super();
	}

	public FileNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message);
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message, cause);

	}

	public FileNotFoundException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public FileNotFoundException(String errorCode, String message) {
		super(errorCode, message);
	}

}