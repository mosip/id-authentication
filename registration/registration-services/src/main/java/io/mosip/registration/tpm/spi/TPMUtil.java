package io.mosip.registration.tpm.spi;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.tpm.asymmetric.RSACipher;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import io.mosip.registration.tpm.sign.Signature;

import tss.Tpm;

/**
 * The TPM Utility class to access the following TPM services: <br>
 * 1. TPM Signature and Validation<br>
 * 2. Get Public Key used for Signing<br>
 * 3. Asymmetric Encryption and Decryption
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMUtil {

	private static final Logger LOGGER = AppConfig.getLogger(TPMUtil.class);

	private TPMUtil() {
	}

	/**
	 * Signs the input data by private key provided from the {@link Tpm}
	 * 
	 * @param dataToSign
	 *            the byte array of the data to be signed
	 * @return the data signed by the TPM
	 */
	public static byte[] signData(byte[] dataToSign) {
		try {
			LOGGER.info(LoggerConstants.TPM_SERVICE_SIGN, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Signing the data by using TPM");

			return Signature.signData(TPMInitialization.getTPMInstance(), dataToSign);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.TPM_UTIL_SIGN_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_SIGN_ERROR.getErrorMessage(), runtimeException);
		}
	}

	/**
	 * Validates the signed data against the actual data using the public part.<br>
	 * The validation requires TSS library but TPM is not required.
	 * 
	 * @param signature
	 *            the signature provided by the {@link Tpm}
	 * @param actualData
	 *            the actual data against which singed data had to be verified
	 * @param publicPart
	 *            the public part of the key which is used for signing the data
	 * @return the response of the signed data validation against actual data
	 */
	public static boolean validateSignature(byte[] signature, byte[] actualData, byte[] publicPart) {
		try {
			LOGGER.info(LoggerConstants.TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_PART,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					"Validating the signature using Public Part");

			return Signature.validateSignatureUsingPublicPart(signature, actualData, publicPart);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_VALIDATE_SIGN_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_VALIDATE_SIGN_ERROR.getErrorMessage(), runtimeException);
		}
	}

	/**
	 * Encrypts the input data by asymmetric algorithm using the TPM
	 * 
	 * @param dataToEncrypt
	 *            the data to be encrypted
	 * @return the encrypted data in bytes
	 */
	public static byte[] asymmetricEncrypt(byte[] dataToEncrypt) {
		try {
			LOGGER.info(LoggerConstants.TPM_SERVICE_ASYMMETRIC_ENCRYPTION, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Encrypting the data by asymmetric algorithm using TPM");

			return RSACipher.encrypt(TPMInitialization.getTPMInstance(), dataToEncrypt);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_ASYMMETRIC_ENCRYPT_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_ASYMMETRIC_ENCRYPT_ERROR.getErrorMessage(),
					runtimeException);
		}
	}

	/**
	 * Decrypts the encrypted data by asymmetric algorithm using the TPM
	 * 
	 * @param encryptedData
	 *            the encrypted data
	 * @return the decrypted data
	 */
	public static byte[] asymmetricDecrypt(byte[] encryptedData) {
		try {
			LOGGER.info(LoggerConstants.TPM_SERVICE_ASYMMETRIC_DECRYPTION, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Decrypting the data by asymmetric algorithm using TPM");

			return RSACipher.decrypt(TPMInitialization.getTPMInstance(), encryptedData);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_ASYMMETRIC_DECRYPT_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_ASYMMETRIC_DECRYPT_ERROR.getErrorMessage(),
					runtimeException);
		}
	}

	/**
	 * Gets the public part of the signing key
	 * 
	 * @return the public part of the signing key
	 */
	public static byte[] getSigningPublicPart() {
		try {
			LOGGER.info(LoggerConstants.TPM_SERVICE_GET_SIGN_PUBLIC, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Decrypting the data by asymmetric algorithm using TPM");

			return Signature.getKey(TPMInitialization.getTPMInstance()).outPublic.toTpm();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_GET_SIGN_KEY_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_GET_SIGN_KEY_ERROR.getErrorMessage(), runtimeException);
		}
	}

	/**
	 * Gets the instance of the {@link Tpm}
	 * 
	 * @return the {@link Tpm} instance
	 */
	public static Tpm getTPMInstance() {
		try {
			return TPMInitialization.getTPMInstance();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_GET_TPM_INSTANCE_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_GET_TPM_INSTANCE_ERROR.getErrorMessage(), runtimeException);
		}
	}

	/**
	 * Closes the {@link Tpm} instance
	 * 
	 * @throws RegBaseCheckedException
	 *             exception while closing the {@link Tpm}
	 */
	public static void closeTPMInstance() throws RegBaseCheckedException {
		try {
			TPMInitialization.closeTPMInstance();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.TPM_UTIL_CLOSE_TPM_INSTANCE_ERROR.getErrorCode(),
					RegistrationExceptionConstants.TPM_UTIL_CLOSE_TPM_INSTANCE_ERROR.getErrorMessage(),
					runtimeException);
		}
	}

}
