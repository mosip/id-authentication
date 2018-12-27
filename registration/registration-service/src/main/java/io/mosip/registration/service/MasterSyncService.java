package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.mastersync.LocationDto;

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

	/**
	 * Find location by hierarchy code.
	 *
	 * @param hierarchyCode the hierarchy code
	 * @param langCode      the lang code
	 * @return the list
	 */
	List<LocationDto> findLocationByHierarchyCode(String hierarchyCode, String langCode);

	/**
	 * Find proviance by hierarchy code.
	 *
	 * @param code the code
	 * @return the list
	 */
	List<LocationDto> findProvianceByHierarchyCode(String code);

}
