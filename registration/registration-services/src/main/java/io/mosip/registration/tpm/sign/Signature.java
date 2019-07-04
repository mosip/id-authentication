package io.mosip.registration.tpm.sign;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMA_OBJECT;
import tss.tpm.TPMS_NULL_SIG_SCHEME;
import tss.tpm.TPMS_PCR_SELECTION;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMS_SIG_SCHEME_RSASSA;
import tss.tpm.TPMT_HA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_SYM_DEF_OBJECT;
import tss.tpm.TPMT_TK_HASHCHECK;
import tss.tpm.TPMU_SIGNATURE;
import tss.tpm.TPM_ALG_ID;
import tss.tpm.TPM_HANDLE;
import tss.tpm.TPM_RH;

/**
 * Class for signing the data using TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Signature {

	private static final Logger LOGGER = AppConfig.getLogger(Signature.class);

	private Signature() {

	}

	/**
	 * Signs the data using private key provided by the TPM
	 * 
	 * @param tpm
	 *            the instance of the {@link Tpm}
	 * @param dataToSign
	 *            the byte arrys of the data to be signed
	 * @return the signed data using the private key
	 */
	public static byte[] signData(Tpm tpm, byte[] dataToSign) {
		LOGGER.info(LoggerConstants.TPM_SIGN_DATA, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Signing data using TPM");

		CreatePrimaryResponse signingKey = getKey(tpm);

		TPMU_SIGNATURE signedData = tpm.Sign(signingKey.handle,
				TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, dataToSign).digest, new TPMS_NULL_SIG_SCHEME(),
				TPMT_TK_HASHCHECK.nullTicket());

		tpm.FlushContext(signingKey.handle);

		LOGGER.info(LoggerConstants.TPM_SIGN_DATA, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Completed Signing data using TPM");

		return ((TPMS_SIGNATURE_RSASSA) signedData).sig;
	}

	/**
	 * Validates the signed data using public part
	 * 
	 * @param signature
	 *            the signature provided by the {@link Tpm}
	 * @param actualData
	 *            the actual data to be validated. The actual data has be be without
	 *            encoding
	 * @param publicPart
	 *            the public part of the key used for signing the data
	 * @return the status of signature
	 */
	public static boolean validateSignatureUsingPublicPart(byte[] signature, byte[] actualData, byte[] publicPart) {
		boolean isSignatureValid = false;

		try {
			LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Validating the signed data using Public Part");

			TPMT_PUBLIC tpmtPublic = TPMT_PUBLIC.fromTpm(publicPart);

			// Create Signature from signed data and algorithm
			TPMU_SIGNATURE rsaSignature = new TPMS_SIGNATURE_RSASSA(TPM_ALG_ID.SHA256, signature);

			LOGGER.info(LoggerConstants.TPM_SIGN_VALIDATE_BY_KEY, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Completed validating the signed data using Public Part");

			// Validate the Signature using Public Template
			isSignatureValid = tpmtPublic.validateSignature(actualData, rsaSignature);
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

	/**
	 * Creates the TPM key for signing the data
	 * 
	 * @param tpm
	 *            the instance of {@link Tpm}
	 * @return the TPM key for signing the data
	 */
	public static CreatePrimaryResponse getKey(Tpm tpm) {
		LOGGER.info(LoggerConstants.LOG_PUBLIC_KEY, RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME, "Creating the Key from Platform TPM for Signing data");

		TPMT_PUBLIC signingKeyPublicPart = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sign,
						TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256), 2048, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		TPM_HANDLE parentHandleForSigningKey = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

		TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(RegistrationConstants.NULL_VECTOR,
				RegistrationConstants.NULL_VECTOR);

		LOGGER.info(LoggerConstants.LOG_PUBLIC_KEY, RegistrationConstants.APPLICATION_ID,
				RegistrationConstants.APPLICATION_NAME,
				"Completed creating the Key from Platform TPM for Signing data");

		return tpm.CreatePrimary(parentHandleForSigningKey, sens, signingKeyPublicPart,
				RegistrationConstants.NULL_VECTOR, new TPMS_PCR_SELECTION[0]);
	}

}