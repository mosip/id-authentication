package io.mosip.registration.tpm.sign;

import tss.Tpm;

/**
 * Interface for signing the data using TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface SignatureService {

	/**
	 * Signs the data using private key provided by the TPM
	 * 
	 * @param tpm
	 *            the instance of the {@link Tpm}
	 * @param dataToSign
	 *            the byte arrys of the data to be signed
	 */
	void signData(Tpm tpm, byte[] dataToSign);

}