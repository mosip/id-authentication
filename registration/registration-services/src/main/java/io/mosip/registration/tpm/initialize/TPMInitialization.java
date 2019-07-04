package io.mosip.registration.tpm.initialize;

import java.io.IOException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

import tss.Tpm;
import tss.TpmFactory;

/**
 * The class to initialize the {@link Tpm}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMInitialization {

	private static final Logger LOGGER = AppConfig.getLogger(TPMInitialization.class);

	private static Tpm tpm;

	private TPMInitialization() {
	}

	/**
	 * Gets the instance of the platform TPM
	 * 
	 * @return the instance of {@link Tpm}
	 */
	public static Tpm getTPMInstance() {
		LOGGER.info(LoggerConstants.LOG_TPM_INITIALIZATION, RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Getting the instance of Platform TPM");

		if (tpm == null) {
			LOGGER.info(LoggerConstants.LOG_TPM_INITIALIZATION, RegistrationConstants.APPLICATION_ID,
					RegistrationConstants.APPLICATION_NAME, "Instantiating the instance of Platform TPM");

			tpm = TpmFactory.platformTpm();
		}

		LOGGER.info(LoggerConstants.LOG_TPM_INITIALIZATION, RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Completed getting the instance of Platform TPM");
		return tpm;
	}

	/**
	 * Closes the {@link Tpm} instance
	 * 
	 * @throws RegBaseCheckedException
	 *             exception while closing the {@link Tpm}
	 */
	public static void closeTPMInstance() throws RegBaseCheckedException {
		LOGGER.info(LoggerConstants.LOG_TPM_INITIALIZATION, RegistrationConstants.APPLICATION_ID, RegistrationConstants.APPLICATION_NAME,
				"Closing the instance of Platform TPM");

		try {
			if (tpm != null) {
				tpm.close();
			}
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.TPM_INIT_CLOSE_TPM_INSTANCE_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_INIT_CLOSE_TPM_INSTANCE_ERROR.getErrorMessage(), ioException);
		}
		LOGGER.info(LoggerConstants.LOG_TPM_INITIALIZATION, RegistrationConstants.APPLICATION_ID, RegistrationConstants.APPLICATION_NAME,
				"Completed closing the instance of Platform TPM");
	}

}
