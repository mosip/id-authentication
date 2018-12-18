package io.mosip.kernel.qrcode.generator.util;

import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.qrcodegenerator.exception.InvalidInputException;
import io.mosip.kernel.qrcode.generator.constant.QrVersion;
import io.mosip.kernel.qrcode.generator.constant.QrcodeExceptionConstants;

/**
 * Utils class for QR code generator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class QrcodegeneratorUtils {
	/**
	 * Constructor for this class
	 */
	private QrcodegeneratorUtils() {

	}

	/**
	 * Verify the input send by user
	 * 
	 * @param data
	 *            data send by user
	 * @param version
	 *            {@link QrVersion} send by user
	 */
	public static void verifyInput(String data, QrVersion version) {
		if (data == null) {
			throw new NullPointerException(QrcodeExceptionConstants.INVALID_INPUT_DATA_NULL.getErrorCode(),
					QrcodeExceptionConstants.INVALID_INPUT_DATA_NULL.getErrorMessage());
		} else if (data.trim().isEmpty()) {
			throw new InvalidInputException(QrcodeExceptionConstants.INVALID_INPUT_DATA_EMPTY.getErrorCode(),
					QrcodeExceptionConstants.INVALID_INPUT_DATA_EMPTY.getErrorMessage());
		} else if (version == null) {
			throw new NullPointerException(QrcodeExceptionConstants.INVALID_INPUT_VERSION.getErrorCode(),
					QrcodeExceptionConstants.INVALID_INPUT_VERSION.getErrorMessage());
		}
	}
}
