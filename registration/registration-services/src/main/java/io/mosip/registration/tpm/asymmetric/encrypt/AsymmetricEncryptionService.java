package io.mosip.registration.tpm.asymmetric.encrypt;

import tss.Tpm;

/**
 * Interface for encrypting the data using asymmetric cryto-alogirthm in TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AsymmetricEncryptionService {

	/**
	 * Encrypts the input data using the TPM
	 * 
	 * @param tpm
	 *            the instance of {@link Tpm}
	 * @param dataToEncrypt
	 *            the data to be encrypted
	 */
	void encryptUsingTPM(Tpm tpm, byte[] dataToEncrypt);

}