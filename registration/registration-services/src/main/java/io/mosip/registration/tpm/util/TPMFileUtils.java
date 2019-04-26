package io.mosip.registration.tpm.util;

import java.io.File;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;

/**
 * The Utility class for file operations used by TPM Services
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMFileUtils {

	private static final Logger LOGGER = AppConfig.getLogger(TPMFileUtils.class);

	private TPMFileUtils() {
	}

	/**
	 * Writes the input bytes to the local file. The input bytes will be encoded
	 * before writing
	 * 
	 * @param fileBytes
	 *            the data to be written to file
	 * @param fileName
	 *            the name of the file
	 */
	public static void writeToFile(byte[] fileBytes, String fileName) {
		try {
			LOGGER.info(LoggerConstants.TPM_FILE_UTILS_WRITE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Writing the data to file");

			FileUtils.writeByteArrayToFile(new File(fileName), CryptoUtil.encodeBase64(fileBytes).getBytes());
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.TPM_FILE_UTILS_WRITE, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, String.format("Error while writing the data to file --> %s",
							ExceptionUtils.getStackTrace(ioException)));
		}
		LOGGER.info(LoggerConstants.TPM_FILE_UTILS_WRITE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Completed writing the data to file");
	}

	public static byte[] readFromFile(String fileName) {
		LOGGER.info(LoggerConstants.TPM_FILE_UTILS_READ, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Reading the data from file");

		byte[] encryptedData = null;
		try {
			encryptedData = CryptoUtil.decodeBase64(new String(FileUtils.readFileToByteArray(new File(fileName))));
		} catch (IOException ioException) {
			LOGGER.error(LoggerConstants.TPM_FILE_UTILS_READ, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, String.format("Error while reading the data from file --> %s",
							ExceptionUtils.getStackTrace(ioException)));
		}
		LOGGER.info(LoggerConstants.TPM_FILE_UTILS_READ, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Completed reading the data from file");

		return encryptedData;
	}
}
