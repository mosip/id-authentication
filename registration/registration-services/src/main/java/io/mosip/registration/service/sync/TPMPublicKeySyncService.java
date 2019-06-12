package io.mosip.registration.service.sync;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Service interface to sync the TPM public key with the server
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface TPMPublicKeySyncService {

	/**
	 * Sync the TPM public key with the server. The key index will be received as
	 * response from the sync service.
	 * 
	 * @return returns the key index
	 * @throws RegBaseCheckedException
	 *             throws checked exception
	 */
	String syncTPMPublicKey() throws RegBaseCheckedException;

}
