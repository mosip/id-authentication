package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncService {

	ResponseDTO getMasterSync(String masterSyncDetails) throws RegBaseCheckedException;

}
