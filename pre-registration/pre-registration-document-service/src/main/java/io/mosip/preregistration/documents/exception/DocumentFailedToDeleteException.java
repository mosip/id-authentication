package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * DocumentNotFoundException
 * 
 * @author M1043008
 *
 */

public class DocumentFailedToDeleteException extends BaseUncheckedException {

	/**
	 * Serial version Id
	 */
	private static final long serialVersionUID = 7303748392658525834L;

	public DocumentFailedToDeleteException() {
		super();
	}

	public DocumentFailedToDeleteException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_011.toString(), message);
	}

	public DocumentFailedToDeleteException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_011.toString(),message, cause);
	}
	
	public DocumentFailedToDeleteException(String errorCode, String message, Throwable cause) {
		super(errorCode,message, cause);
	}

}
