package io.mosip.registration.tpm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.tpm.TPMService;
import io.mosip.registration.tpm.asymmetric.decrypt.AsymmetricDecryptionService;
import io.mosip.registration.tpm.asymmetric.encrypt.AsymmetricEncryptionService;
import io.mosip.registration.tpm.initialize.PlatformTPMInitialization;
import io.mosip.registration.tpm.sign.SignatureService;
import io.mosip.registration.tpm.sign.SignatureValidationService;

/**
 * The TPM service implementation class to access the following TPM services:
 * <br>
 * 1. TPM Signature and Validation<br>
 * 2. Asymmetric Encryption and Decryption
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class TPMServiceImpl implements TPMService {

	private static final Logger LOGGER = AppConfig.getLogger(TPMServiceImpl.class);

	@Autowired
	private SignatureService dataSignServiceImpl;
	@Autowired
	private SignatureValidationService signatureValidation;
	@Autowired
	private AsymmetricEncryptionService asymmetricEncryptionServiceImpl;
	@Autowired
	private AsymmetricDecryptionService asymmetricDecryptionServiceImpl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.TPMService#signData(byte[])
	 */
	@Override
	public void signData(byte[] dataToSign) {
		LOGGER.info(LoggerConstants.TPM_SERVICE_SIGN, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Signing the data by using TPM");

		dataSignServiceImpl.signData(PlatformTPMInitialization.getTPMInstance(), dataToSign);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.tpm.TPMService#validateSignatureUsingPublicPart(byte[],
	 * byte[])
	 */
	@Override
	public boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData) {
		LOGGER.info(LoggerConstants.TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_PART, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the signature using Public Part");

		return signatureValidation.validateSignatureUsingPublicPart(signedData, actualData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.tpm.TPMService#validateSignatureUsingPublicKey(byte[],
	 * byte[])
	 */
	@Override
	public boolean validateSignatureUsingPublicKey(byte[] signedData, byte[] actualData) {
		LOGGER.info(LoggerConstants.TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_KEY, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Validating the signature using Public Key");

		return signatureValidation.validateSignatureUsingPublicKey(signedData, actualData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.TPMService#asymmetricEncrypt(byte[])
	 */
	@Override
	public void asymmetricEncrypt(byte[] dataToEncrypt) {
		LOGGER.info(LoggerConstants.TPM_SERVICE_ASYMMETRIC_ENCRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Encrypting the data by asymmetric algorithm using TPM");

		asymmetricEncryptionServiceImpl.encryptUsingTPM(PlatformTPMInitialization.getTPMInstance(), dataToEncrypt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.TPMService#asymmetricDecrypt(byte[])
	 */
	@Override
	public byte[] asymmetricDecrypt(byte[] encryptedData) {
		LOGGER.info(LoggerConstants.TPM_SERVICE_ASYMMETRIC_DECRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Decrypting the data by asymmetric algorithm using TPM");

		return asymmetricDecryptionServiceImpl.decryptUsingTPM(PlatformTPMInitialization.getTPMInstance(),
				encryptedData);
	}

}
