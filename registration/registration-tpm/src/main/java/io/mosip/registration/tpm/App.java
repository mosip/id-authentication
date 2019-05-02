package io.mosip.registration.tpm;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.service.TPMPublicKey;
import io.mosip.registration.tpm.service.impl.TPMPublicKeyImpl;
import io.mosip.registration.tpm.util.TPMFileUtils;

/**
 * Starter Class containing the main method. <br>
 * This class gets the TPM Public Key and stores to a file.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class App {

	private static final Logger LOGGER = TPMLogger.getLogger(App.class);

	/**
	 * This method gets the TPM Public Key and stores to a file.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			LOGGER.info(Constants.LOG_APP, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Starting the application");

			TPMPublicKey tpmPublicKey = new TPMPublicKeyImpl();
			TPMFileUtils.writeToFile(Constants.PUBLIC_PART_FILE_NAME, tpmPublicKey.getPublicKey());
		} catch (Exception exception) {
			LOGGER.error(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					String.format("Exception while getting the Public Key from Platform TPM --> %s",
							ExceptionUtils.getStackTrace(exception)));
		} finally {
			try {
				TPMInitialization.closeTPMInstance();
			} catch (Exception tpmCloseException) {
				LOGGER.error(Constants.LOG_TPM_INITIALIZATION, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
						String.format("Exception while getting the Public Key from Platform TPM --> %s",
								ExceptionUtils.getStackTrace(tpmCloseException)));
			}

			LOGGER.info(Constants.LOG_APP, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Closing the application");

			System.exit(0);
		}
	}
}
