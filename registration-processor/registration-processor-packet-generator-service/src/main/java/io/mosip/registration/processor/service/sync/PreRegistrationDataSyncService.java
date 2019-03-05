package io.mosip.registration.processor.service.sync;

import io.mosip.registration.processor.packet.service.dto.ResponseDTO;

/**
 * Pre Registration Data Sync Service
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncService {

	
	/**
	 * Retrive pre registrations
	 * @param syncJobId the job which it triggered
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistrationIds(String syncJobId);

	
	/**
	 * Get Pre Registration
	 * @param preRegistrationId preRegId
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistration(String preRegistrationId);
	
	/**
	 * Fetch All the Pre-Registration Records that needs to be deleted and delete those records
	 * @return
	 */
	public ResponseDTO fetchAndDeleteRecords();
	
	
}
