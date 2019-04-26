package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * The Interface for Public Key.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
 
public interface PublicKeySync {

	/**
	 * Gets the public key.
	 *
	 * @param triggerPoint the trigger point
	 * @return the public key
	 */
	public ResponseDTO getPublicKey(String triggerPoint);
	
}
