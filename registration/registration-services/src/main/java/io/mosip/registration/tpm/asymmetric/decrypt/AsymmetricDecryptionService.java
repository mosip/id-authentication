package io.mosip.registration.tpm.asymmetric.decrypt;

import tss.Tpm;

/**
 * Interface for decrypting the encrypted data using asymmetric cryto-alogirthm
 * in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AsymmetricDecryptionService {

	/**
	 * Decrypts the encrypted data using the {@link Tpm} instance
	 * 
	 * @param tpm
	 *            the instance of the {@link Tpm}
	 * @param encryptedData
	 *            the encrypted data
	 * @return the byte array of decrypted data
	 */
	byte[] decryptUsingTPM(Tpm tpm, byte[] encryptedData);

}