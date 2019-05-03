package io.mosip.registration.tpm.sign;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMU_SIGNATURE;
import tss.tpm.TPM_ALG_ID;

/**
 * Class for validating the signature data
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class SignatureValidationService {

	private static final Logger LOGGER = TPMLogger.getLogger(SignatureValidationService.class);

	/**
	 * Validates the signed data using public part
	 * 
	 * @param signedData
	 *            the data to be signed
	 * @param actualData
	 *            the actual data to be validated
	 * @param publicPart
	 *            the public part of the key used for signing the data
	 * @return the status of signed data
	 */
	public boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData, byte[] publicPart) {
		boolean isSignatureValid = false;

		try {
			LOGGER.info(Constants.TPM_SIGN_VALIDATE_BY_KEY, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
					"Validating the signed data using Public Part");

			TPMT_PUBLIC tpmtPublic = TPMT_PUBLIC.fromTpm(publicPart);

			// Create Signature from signed data and algorithm
			TPMU_SIGNATURE signature = new TPMS_SIGNATURE_RSASSA(TPM_ALG_ID.SHA256, signedData);

			LOGGER.info(Constants.TPM_SIGN_VALIDATE_BY_KEY, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
					"Completed validating the signed data using Public Part");

			// Validate the Signature using Public Template
			isSignatureValid = tpmtPublic.validateSignature(actualData, signature);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(Constants.TPM_SIGN_VALIDATE_BY_KEY, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
					String.format("Error while validating the signed data using Public Part --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));
		}

		LOGGER.info(Constants.TPM_SIGN_VALIDATE_BY_KEY, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Completed validating the signed data using Public Part");

		return isSignatureValid;
	}

}