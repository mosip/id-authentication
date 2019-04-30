package io.mosip.registration.tpm.asymmetric.encrypt.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.tpm.asymmetric.AsymmetricKeyCreation;
import io.mosip.registration.tpm.asymmetric.encrypt.AsymmetricEncryptionService;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.util.TPMFileUtils;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * The service implementation for encrypting the data using TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class AsymmetricEncryptionServiceImpl extends AsymmetricKeyCreation implements AsymmetricEncryptionService {

	private static final Logger LOGGER = AppConfig.getLogger(AsymmetricEncryptionServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.tpm.asymmetric.encrypt.AsymmetricEncryptionService#
	 * encryptUsingTPM(tss.Tpm, byte[])
	 */
	@Override
	public void encryptUsingTPM(Tpm tpm, byte[] dataToEncrypt) {
		LOGGER.info(LoggerConstants.TPM_ASYM_ENCRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Encrypting the data by asymmetric algorithm using TPM");

		byte[] encryptedData = tpm.RSA_Encrypt(createPersistentKey(tpm), dataToEncrypt, new TPMS_NULL_ASYM_SCHEME(),
				Constants.NULL_VECTOR);
		TPMFileUtils.writeToFile(encryptedData, Constants.ASYMMETRIC_ENCRYPTED_DATA_FILE_NAME);

		LOGGER.info(LoggerConstants.TPM_ASYM_ENCRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID,
				"Completed encrypting the data by asymmetric algorithm using TPM");
	}

}
