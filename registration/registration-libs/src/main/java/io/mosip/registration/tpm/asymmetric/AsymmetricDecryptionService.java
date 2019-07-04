package io.mosip.registration.tpm.asymmetric;

import tss.Tpm;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;

/**
 * Class for decrypting the encrypted data using asymmetric cryto-alogirthm
 * in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class AsymmetricDecryptionService {

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
		return new String(tpm.RSA_Decrypt(asymmetricKeyCreationService.createPersistentKey(tpm), encryptedData,
				new TPMS_NULL_ASYM_SCHEME(), new byte[0])).trim().getBytes();
	}

}