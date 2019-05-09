package io.mosip.registration.service.sync;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;

/**
 * Pre Registration Data Sync Service
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncService {

	/**
	 * Retrive pre registrations from the pre reg server
	 * 
	 * @param syncJobId
	 *            the job which it triggered
	 * @return ResponseDTO response data
	 */
	public ResponseDTO getPreRegistrationIds(String syncJobId);

	/**
	 * Get Pre Registration from pre gre server or reg client db
	 * 
	 * @param preRegistrationId
	 *            preRegId
	 * @return ResponseDTO response data
	 */
	public ResponseDTO getPreRegistration(String preRegistrationId);

	/**
	 * Fetch All the Pre-Registration Records that needs to be deleted and delete
	 * those records
	 * 
	 * @return ResponseDTO - holds response data
	 */
	public ResponseDTO fetchAndDeleteRecords();

	/**
	 * Deletes pre registration records.
	 *
	 * @param responseDTO 
	 * 				the response DTO
	 * @param preRegList 
	 * 				the pre registration list
	 */
	public void deletePreRegRecords(ResponseDTO responseDTO, final List<PreRegistrationList> preRegList);
	
	/**
	 * Gets the pre registration record for deletion.
	 *
	 * @param preRegistrationId 
	 * 				the pre registration id
	 * @return the pre registration record for deletion
	 */
	public PreRegistrationList getPreRegistrationRecordForDeletion(String preRegistrationId);
}
