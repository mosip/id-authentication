package io.mosip.registration.tpm.sign.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.sign.SignatureValidationService;
import io.mosip.registration.tpm.util.TPMFileUtils;

import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMS_SIG_SCHEME_RSASSA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMU_SIGNATURE;
import tss.tpm.TPM_ALG_ID;

/**
 * The service implementation class to validate the signature provided by the
 * TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class SignatureValidationServiceImpl implements SignatureValidationService {

	private static final Logger LOGGER = AppConfig.getLogger(SignatureValidationServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.sign.SignatureValidationService#
	 * validateSignatureUsingPublicKey(byte[], byte[])
	 */
	@Override
	public boolean validateSignatureUsingPublicKey(byte[] signedData, byte[] actualData) {
		boolean isSignatureValid = false;

		try {
			LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the signed data using Public Key");

			// Create Public Template from Public Key
			byte[] publicKeyBytes = TPMFileUtils.readFromFile(Constants.PUBLIC_KEY_FILE_NAME);

			TPMT_PUBLIC tpmtPublic = new TPMT_PUBLIC();

			// Set the Public Key to template
			tpmtPublic.unique = new TPM2B_PUBLIC_KEY_RSA(publicKeyBytes);

			// Set Public Keys Params --> Schema, Exponent and Key Length
			TPMS_RSA_PARMS tpmsRSAParams = new TPMS_RSA_PARMS();
			tpmsRSAParams.scheme = new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256);
			tpmsRSAParams.exponent = 65537;
			tpmsRSAParams.keyBits = 2048;
			tpmtPublic.parameters = tpmsRSAParams;

			// Create Signature from signed data and algorithm
			TPMU_SIGNATURE signature = new TPMS_SIGNATURE_RSASSA(TPM_ALG_ID.SHA256, signedData);

			// Validate the Signature using Public Template
			isSignatureValid = tpmtPublic.validateSignature(Constants.DATA_TO_SIGN, signature);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					String.format("Error while validating the signed data using Public Key --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));
		}

		LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Completed validating the signed data using Public Key");

		return isSignatureValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.sign.SignatureValidationService#
	 * validateSignatureUsingPublicPart(byte[], byte[])
	 */
	@Override
	public boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData) {
		boolean isSignatureValid = false;

		try {
			LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the signed data using Public Part");

			TPMT_PUBLIC tpmtPublic = TPMT_PUBLIC.fromTpm(TPMFileUtils.readFromFile(Constants.PUBLIC_PART_FILE_NAME));

			// Create Signature from signed data and algorithm
			TPMU_SIGNATURE signature = new TPMS_SIGNATURE_RSASSA(TPM_ALG_ID.SHA256, signedData);

			LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Completed validating the signed data using Public Part");

			// Validate the Signature using Public Template
			isSignatureValid = tpmtPublic.validateSignature(actualData, signature);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					String.format("Error while validating the signed data using Public Part --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));
		}

		LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Completed validating the signed data using Public Part");

		return isSignatureValid;
	}

}
