package io.mosip.registration.tpm;

import java.io.IOException;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

import tss.Tpm;
import tss.TpmFactory;

/**
 * The class to initialize the {@link Tpm}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMInitialization {

	private static final Logger LOGGER = TPMLogger.getLogger(TPMInitialization.class);

	private static Tpm tpm;

	private TPMInitialization() {
	}

	/**
	 * Gets the instance of the platform TPM
	 * 
	 * @return the instance of {@link Tpm}
	 */
	public static Tpm getTPMInstance() {
		try {
			LOGGER.info(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Getting the instance of Platform TPM");

			if (tpm == null) {
				LOGGER.info(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
						"Instantiating the instance of Platform TPM");

				tpm = TpmFactory.platformTpm();
			}

			LOGGER.info(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Completed getting the instance of Platform TPM");
			return tpm;
		} catch (RuntimeException runtimeException) {
			LOGGER.error(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					String.format("Exception while getting the instance of Platform TPM --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));

			throw runtimeException;
		}
	}

	/**
	 * Closes the {@link Tpm} instance
	 * 
	 * @throws IOException
	 *             exception while closing the {@link Tpm}
	 */
	public static void closeTPMInstance() throws IOException {
		LOGGER.info(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
				"Closing the instance of Platform TPM");

		try {
			if (tpm != null) {
				tpm.close();
			}
		} catch (IOException ioException) {
			LOGGER.error(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					String.format("Exception while closing the instance of Platform TPM --> %s",
							ExceptionUtils.getStackTrace(ioException)));

			throw ioException;
		}
		LOGGER.info(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
				"Completed closing the instance of Platform TPM");
	}

}
