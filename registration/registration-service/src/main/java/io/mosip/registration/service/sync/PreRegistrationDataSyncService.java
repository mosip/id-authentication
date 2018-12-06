package io.mosip.registration.service.sync;

import java.util.List;

import io.mosip.registration.dto.ResponseDTO;

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

	
}
