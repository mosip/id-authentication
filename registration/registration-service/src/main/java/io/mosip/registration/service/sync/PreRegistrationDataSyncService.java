package io.mosip.registration.service.sync;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.PreRegistrationList;

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
	
	/**
	 * Update the deleted Pre-Reg Records in the Table
	 * @param preRegList
	 * @return
	 */
	public ResponseDTO updateDeletedRecords(List<PreRegistrationList> preRegList);

	
}
