package io.mosip.registration.tpm.service;

/**
 * Interface to get the Public Key of the underlying TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface TPMPublicKey {

	/**
	 * Returns the Public Key as bytes of the underlying TPM
	 * 
	 * @return the bytes of TPM's Public Key
	 */
	byte[] getPublicKey();

}
