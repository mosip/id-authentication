package io.mosip.util;

import org.apache.log4j.Logger;

/**
 * Utils class for QR code generator
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class QrcodegeneratorUtils {
	private static Logger logger = Logger.getLogger(QrcodegeneratorUtils.class);
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
			logger.error("Null value for data");
		} else if (data.trim().isEmpty()) {
			logger.error("invalid input");
		} else if (version == null) {
			logger.error("Null value for version");
		}
	}
}
