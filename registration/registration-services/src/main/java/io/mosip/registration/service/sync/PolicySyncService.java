package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;

/**
 * It provides the method to download the Mosip public key specific to the user's local machines and center specific and store 
 * the same into local db for further usage during registration process. The key has expiry period. Based on the expiry period the 
 * new key would be downloaded from the server through this service by triggering from batch process.  
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncService {
	/**
	 * It checks the local db to find whether the public key is available. If available and not expired then uses the key from db. 
	 * If not available or about to expire [based on threshold value] then download the key from external service 'policy sync' and update the db.
	 * 
	 * @return ResponseDTO
	 */
	ResponseDTO fetchPolicy();

	/**
	 * It fetches the public key based on center id and machine id based combination from local database. If key available then validate the 
	 * key expiry datetime along with the threshold value. 
	 * 
	 * @return ResponseDTO
	 * 			return success object if key not expired or otherwise return false object.  
	 * 			
	 */
	ResponseDTO checkKeyValidation();
}
