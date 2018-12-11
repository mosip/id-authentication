package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;

/**
 * DocumentSizeExceedException
 * 
 * @author M1043008
 *
 */
public class DocumentSizeExceedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4879473387592007255L;

	public DocumentSizeExceedException() {
		super();

	}

	public DocumentSizeExceedException(String message, Throwable cause) {
		super(ErrorCodes.PRG_PAM_DOC_007.toString(), message, cause);

	}

	public DocumentSizeExceedException(String message) {
		super(ErrorCodes.PRG_PAM_DOC_007.toString(), message);

	}

}
