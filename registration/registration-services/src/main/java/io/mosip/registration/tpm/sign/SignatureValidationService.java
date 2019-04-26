package io.mosip.registration.tpm.sign;

import tss.Tpm;

/**
 * Interface for validating the signature data
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface SignatureValidationService {

	/**
	 * Validates the signed data using public key
	 * 
	 * @param signedData
	 *            the data signed using the private key provided by {@link Tpm}
	 * @param actualData
	 *            the data to be verified
	 * @return the status of signed data
	 */
	boolean validateSignatureUsingPublicKey(byte[] signedData, byte[] actualData);

	/**
	 * Validates the signed data using public part
	 * 
	 * @param signedData
	 *            the data to be signed
	 * @param actualData
	 *            the actual data to be validated
	 * @return the status of signed data
	 */
	boolean validateSignatureUsingPublicPart(byte[] signedData, byte[] actualData);

}