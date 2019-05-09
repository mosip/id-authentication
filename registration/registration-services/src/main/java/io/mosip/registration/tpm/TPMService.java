package io.mosip.registration.tpm;

import tss.Tpm;

/**
 * The service interface to access the TPM services - Signature and Asymmetric
 * Encryption and Decryption.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface TPMService {

	/**
	 * Signs the input data by private key provided from the {@link Tpm}
	 * 
	 * @param dataToSign
	 *            the byte array of the data to be signed
	 */
	void signData(byte[] dataToSign);

	/**
	 * Validates the signed data against the actual data using the public part
	 * 
	 * @param signedData
	 *            the signed data
	 * @param actualData
	 *            the actual data against which singed data had to be verified
	 * @return the response of the signed data validation against actual data
	 */
	boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData);

	/**
	 * Validates the signed data against the actual data using the public key
	 * 
	 * @param signedData
	 *            the signed data
	 * @param actualData
	 *            the actual data against which singed data had to be verified
	 * @return the response of the signed data validation against actual data
	 */
	boolean validateSignatureUsingPublicKey(byte[] signedData, byte[] actualData);

	/**
	 * Encrypts the input data by asymmetric algorithm using the TPM
	 * 
	 * @param dataToEncrypt
	 *            the data to be encrypted
	 */
	void asymmetricEncrypt(byte[] dataToEncrypt);

	/**
	 * Decrypts the encrypted data by asymmetric algorithm using the TPM
	 * 
	 * @param encryptedData
	 *            the encrypted data
	 * @return the decypted data
	 */
	byte[] asymmetricDecrypt(byte[] encryptedData);

}
