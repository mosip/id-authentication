package io.mosip.registration.tpm.asymmetric;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * Interface for decrypting the encrypted data using asymmetric cryto-alogirthm
 * in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class AsymmetricDecryptionService {

	private static final Logger LOGGER = TPMLogger.getLogger(AsymmetricDecryptionService.class);
	private AsymmetricKeyCreationService asymmetricKeyCreationService = new AsymmetricKeyCreationService();

	/**
	 * Decrypts the encrypted data using the {@link Tpm} instance
	 * 
	 * @param tpm
	 *            the instance of the {@link Tpm}
	 * @param encryptedData
	 *            the encrypted data
	 * @return the byte array of decrypted data
	 */
	public byte[] decryptUsingTPM(Tpm tpm, byte[] encryptedData) {
		LOGGER.info(Constants.TPM_ASYM_DECRYPTION, Constants.APPLICATION_NAME, Constants.APPLICATION_ID,
				"Decrypting the data by asymmetric algorithm using TPM");

		return new String(tpm.RSA_Decrypt(asymmetricKeyCreationService.createPersistentKey(tpm), encryptedData,
				new TPMS_NULL_ASYM_SCHEME(), Constants.NULL_VECTOR)).trim().getBytes();
	}

}