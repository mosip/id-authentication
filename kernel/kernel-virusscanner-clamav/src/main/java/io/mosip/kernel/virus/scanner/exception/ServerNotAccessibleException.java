/**
 * 
 * 
 */
package io.mosip.kernel.virus.scanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.virus.scanner.exception.util.VirusScannerErrorCodes;

/**
 * TheServerNotAccessibleException occurs when anti virus service is not
 * responding
 * 
 * @author Mukul Puspam
 * 
 */
public class ServerNotAccessibleException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerNotAccessibleException() {
		super();
	}

	public ServerNotAccessibleException(String errorMessage) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE, errorMessage);
	}

	public ServerNotAccessibleException(String message, Throwable cause) {
		super(VirusScannerErrorCodes.IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE, message, cause);
	}

}
