package io.mosip.preregistration.generateQRcode.exception.util;

import java.io.IOException;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.preregistration.generateQRcode.error.ErrorCodes;
import io.mosip.preregistration.generateQRcode.error.ErrorMessages;
import io.mosip.preregistration.generateQRcode.exception.IllegalParamException;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
public class QRcodeExceptionCatcher {
	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex) {
		if (ex instanceof QrcodeGenerationException) {
			throw new IllegalParamException(ErrorCodes.PRG_QRC_002.getCode(),
					ErrorMessages.QRCODE_FAILED_TO_GENERATE.getCode(),ex.getCause());
		}
		else if (ex instanceof IOException) {
			throw new io.mosip.preregistration.generateQRcode.exception.IOException(ErrorCodes.PRG_QRC_001.getCode(),
						ErrorMessages.INPUT_OUTPUT_EXCEPTION.getCode(),ex.getCause());
}
		else if (ex instanceof NullPointerException) {
			throw new IllegalParamException(ErrorCodes.PRG_QRC_002.getCode(),
					ErrorMessages.QRCODE_FAILED_TO_GENERATE.getCode(),ex.getCause());
		}
	}
}
