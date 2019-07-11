/**
 * 
 * 
 */
package io.mosip.kernel.core.virusscanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * TheServerNotAccessibleException occurs when anti virus service is not
 * responding
 * 
 * @author Mukul Puspam
 * 
 */
public class VirusScannerException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VirusScannerException() {
		super();
	}

	public VirusScannerException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public VirusScannerException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
