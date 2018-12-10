package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * DocumentNotFoundException
 * 
 * @author M1043008
 *
 */

public class DocumentNotFoundException extends BaseUncheckedException {

	/**
	 * Serial version Id
	 */
	private static final long serialVersionUID = 7303748392658525834L;

	public DocumentNotFoundException() {
		super();
	}

	public DocumentNotFoundException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(), message);
	}

	public DocumentNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_005.toString(),message, cause);
	}

}
