package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * DocumentNotFoundException
 * 
 * @author M1043008
 *
 */

public class DocumentFailedToUploadException extends BaseUncheckedException {

	/**
	 * Serial version Id
	 */
	private static final long serialVersionUID = 7303748392658525834L;

	public DocumentFailedToUploadException() {
		super();
	}

	public DocumentFailedToUploadException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(), message);
	}

	public DocumentFailedToUploadException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_009.toString(),message, cause);
	}
	
	public DocumentFailedToUploadException(String errorCode, String message, Throwable cause) {
		super(errorCode,message, cause);
	}
	
	public DocumentFailedToUploadException(String errorCode, String message) {
		super(errorCode,message);
	}


}
