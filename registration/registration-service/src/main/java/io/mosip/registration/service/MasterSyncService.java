package io.mosip.registration.service;

import java.util.Map;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to sync master data from server to client
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncService {

	
	Map<String,Object> getMasterSync(String masterSyncDetails) throws RegBaseCheckedException;
	
	
}
