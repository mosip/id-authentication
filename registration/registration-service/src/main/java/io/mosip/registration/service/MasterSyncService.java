package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Interface to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncService {

	/**
	 * Gets the master sync.
	 *
	 * @param masterSyncDetails the master sync details
	 * @return the master sync
	 */
	ResponseDTO getMasterSync(String masterSyncDetails);

}
