package io.mosip.kernel.core.idgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for VID Generation Failed Exception
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
public class VidGenerationFailedException extends BaseUncheckedException {
	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = -6990502141757024297L;





	/**
	 * Constructor initialize VIDGenerationFailedException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public VidGenerationFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
