package io.mosip.registration.service.sync;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Service interface to sync the local machine's TPM public key and machine name with the server. Based on success criteria, 
 * this would receive the 'key index' from server. This 'key index' would be used while performing the master sync with the server.  
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface TPMPublicKeySyncService {

	/**
	 * Sync the TPM public key with the server along with the machine name. The key index will be received as
	 * response from the sync service.
	 * 
	 * @return returns the key index
	 * @throws RegBaseCheckedException
	 *             throws checked exception
	 */
	String syncTPMPublicKey() throws RegBaseCheckedException;

}
