package io.mosip.registration.tpm.spi;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.asymmetric.AsymmetricDecryptionService;
import io.mosip.registration.tpm.asymmetric.AsymmetricEncryptionService;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.initialize.TPMInitialization;
import io.mosip.registration.tpm.sign.SignKeyCreationService;
import io.mosip.registration.tpm.sign.SignatureService;
import io.mosip.registration.tpm.sign.SignatureValidationService;

import tss.Tpm;

/**
 * The TPM service class to access the following TPM services: <br>
 * 1. TPM Signature and Validation<br>
 * 2. Get Public Key used for Signing<br>
 * 3. Asymmetric Encryption and Decryption
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMService {

	private static final Logger LOGGER = TPMLogger.getLogger(TPMService.class);

	private SignKeyCreationService signKeyCreationService = new SignKeyCreationService();
	private SignatureService dataSignService = new SignatureService();
	private SignatureValidationService signatureValidation = new SignatureValidationService();
	private AsymmetricEncryptionService asymmetricEncryptionService = new AsymmetricEncryptionService();
	private AsymmetricDecryptionService asymmetricDecryptionService = new AsymmetricDecryptionService();

	/**
	 * Signs the input data by private key provided from the {@link Tpm}
	 * 
	 * @param dataToSign
	 *            the byte array of the data to be signed
	 * @return the data signed by the TPM
	 */
	public byte[] signData(byte[] dataToSign) {
		LOGGER.info(Constants.TPM_SERVICE_SIGN, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Signing the data by using TPM");

		return dataSignService.signData(TPMInitialization.getTPMInstance(), dataToSign);
	}

	/**
	 * Validates the signed data against the actual data using the public part
	 * 
	 * @param signedData
	 *            the signed data
	 * @param actualData
	 *            the actual data against which singed data had to be verified
	 * @param publicPart
	 *            the public part of the key which is used for signing the data
	 * @return the response of the signed data validation against actual data
	 */
	public boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData, byte[] publicPart) {
		LOGGER.info(Constants.TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_PART, Constants.APPLICATION_NAME,
				Constants.APPLICATION_ID, "Validating the signature using Public Part");

		return signatureValidation.validateSignatureUsingPublicPart(signedData, actualData, publicPart);
	}

	/**
	 * Encrypts the input data by asymmetric algorithm using the TPM
	 * 
	 * @param dataToEncrypt
	 *            the data to be encrypted
	 * @return the encrypted data in bytes
	 */
	public byte[] asymmetricEncrypt(byte[] dataToEncrypt) {
		LOGGER.info(Constants.TPM_SERVICE_ASYMMETRIC_ENCRYPTION, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Encrypting the data by asymmetric algorithm using TPM");

		return asymmetricEncryptionService.encryptUsingTPM(TPMInitialization.getTPMInstance(), dataToEncrypt);
	}

	/**
	 * Decrypts the encrypted data by asymmetric algorithm using the TPM
	 * 
	 * @param encryptedData
	 *            the encrypted data
	 * @return the decrypted data
	 */
	public byte[] asymmetricDecrypt(byte[] encryptedData) {
		LOGGER.info(Constants.TPM_SERVICE_ASYMMETRIC_DECRYPTION, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Decrypting the data by asymmetric algorithm using TPM");

		return asymmetricDecryptionService.decryptUsingTPM(TPMInitialization.getTPMInstance(), encryptedData);
	}

	/**
	 * Gets the public part of the signing key
	 * 
	 * @return the public part of the signing key
	 */
	public byte[] getSigningPublicPart() {
		LOGGER.info(Constants.TPM_SERVICE_GET_SIGN_PUBLIC, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Decrypting the data by asymmetric algorithm using TPM");

		return signKeyCreationService.getKey(TPMInitialization.getTPMInstance()).outPublic.toTpm();
	}

}
