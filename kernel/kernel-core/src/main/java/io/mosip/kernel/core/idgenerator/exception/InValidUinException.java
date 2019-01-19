package io.mosip.kernel.core.idgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for Invalid Uin Exception
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
public class InValidUinException extends BaseUncheckedException {
	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = 1L;





	/**
	 * Constructor initialize InValidUinException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public InValidUinException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
