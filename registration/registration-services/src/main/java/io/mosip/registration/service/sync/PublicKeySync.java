package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;

/**
 * It downloads the Mosip public key from server and store the same into local database for further usage. 
 * The stored key will be used to validate the signature provided in the external services response. If signature doesn't match then 
 * response would be rejected and error response would be sent to the invoking client application. 
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface PublicKeySync {

	/**
	 * Connect to the external service 'publickey' and download the MOSIP public key. Then update the same into the local db.  
	 * Before downloading it checks for the key availability in local db. If available, then check for the key expiry period. If already 
	 * expired then make the online connectivity and download the new key. If not expired then it won't download the key and uses the key available in db. 
	 * 
	 * This service is called from Scheduler to donwload the key at regular interval after does the required validaiton. 
	 * 
	 * @param triggerPoint the trigger point
	 * 		User or System. 
	 * @return 
	 * 		Success or failure response along with error code. 
	 */
	public ResponseDTO getPublicKey(String triggerPoint);
	
}
