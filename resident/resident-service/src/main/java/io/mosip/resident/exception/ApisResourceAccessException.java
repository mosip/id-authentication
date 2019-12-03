package io.mosip.resident.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

/**
 * The Class ApisResourceAccessException.
 * 
 */
public class ApisResourceAccessException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new apis resource access exception.
	 */
	public ApisResourceAccessException() {
		super();
	}

	/**
	 * Instantiates a new apis resource access exception.
	 *
	 * @param message the message
	 */
	public ApisResourceAccessException(String message) {
		super(ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode(), message);
	}

	/**
	 * Instantiates a new apis resource access exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ApisResourceAccessException(String message, Throwable cause) {
		super(ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode(), message, cause);
	}
}