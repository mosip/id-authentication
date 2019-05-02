package io.mosip.registration.tpm.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

/**
 * The Utility class for file operations used by TPM Services
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMFileUtils {

	private static final Logger LOGGER = TPMLogger.getLogger(TPMFileUtils.class);

	private TPMFileUtils() {
	}

	/**
	 * Writes the input bytes to the local file. The input bytes will be encoded
	 * before writing
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param data
	 *            the data to be written to file
	 * @exception IOException
	 *                exception while writing data to file
	 */
	public static void writeToFile(String fileName, byte[] data) throws IOException {
		LOGGER.info(Constants.LOG_TPM_FILE_UTILS, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
				"Writing the Public Key to file");

		try {
			FileUtils.writeByteArrayToFile(new File(fileName), Base64.encodeBase64(data));

			LOGGER.info(Constants.LOG_TPM_FILE_UTILS, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Completed writing the Public Key to file");
		} catch (RuntimeException runtimeException) {
			LOGGER.error(Constants.LOG_TPM_FILE_UTILS, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					String.format("Exception while writing the Public Key to file --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));

			throw runtimeException;
		}
	}

}
