package io.mosip.registration.tpm.asymmetric.decrypt.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.tpm.asymmetric.AsymmetricKeyCreation;
import io.mosip.registration.tpm.asymmetric.decrypt.AsymmetricDecryptionService;
import io.mosip.registration.tpm.constants.Constants;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * The service implementation for decrypting the encrypted data using TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class AsymmetricDecryptionServiceImpl extends AsymmetricKeyCreation implements AsymmetricDecryptionService {

	private static final Logger LOGGER = AppConfig.getLogger(AsymmetricDecryptionServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.tpm.asymmetric.decrypt.AsymmetricDecryptionService#
	 * decryptUsingTPM(tss.Tpm, byte[])
	 */
	@Override
	public byte[] decryptUsingTPM(Tpm tpm, byte[] encryptedData) {
		LOGGER.info(LoggerConstants.TPM_ASYM_DECRYPTION, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Decrypting the data by asymmetric algorithm using TPM");

		return new String(tpm.RSA_Decrypt(createPersistentKey(tpm), encryptedData, new TPMS_NULL_ASYM_SCHEME(),
				Constants.NULL_VECTOR)).trim().getBytes();
	}

}
