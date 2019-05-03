package io.mosip.registration.tpm.asymmetric;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * Class for encrypting the data using asymmetric cryto-alogirthm in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class AsymmetricEncryptionService {

	private static final Logger LOGGER = TPMLogger.getLogger(AsymmetricEncryptionService.class);
	private AsymmetricKeyCreationService asymmetricKeyCreationService = new AsymmetricKeyCreationService();

	/**
	 * Encrypts the input data using the TPM
	 * 
	 * @param tpm
	 *            the instance of {@link Tpm}
	 * @param dataToEncrypt
	 *            the data to be encrypted
	 * @return returns the TPM encrypted data
	 */
	public byte[] encryptUsingTPM(Tpm tpm, byte[] dataToEncrypt) {
		LOGGER.info(Constants.TPM_ASYM_ENCRYPTION, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Encrypting the data by asymmetric algorithm using TPM");

		return tpm.RSA_Encrypt(asymmetricKeyCreationService.createPersistentKey(tpm), dataToEncrypt,
				new TPMS_NULL_ASYM_SCHEME(), Constants.NULL_VECTOR);
	}

}