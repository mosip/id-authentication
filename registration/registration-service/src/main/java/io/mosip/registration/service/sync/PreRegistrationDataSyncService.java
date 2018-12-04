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
	 * Retriev all preregistartion id's
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistrationIds();
	
	/**
	 * Retrieve pre registrations
	 * @param syncJobId the job which it triggered
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistrationIds(String syncJobId);

	/**
	 * get list of pre Registartions
	 * @param preRegId list
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistrations(List<String> preRegId);

	/**
	 * Get Pre Registration
	 * @param preRegistrationId preRegId
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistration(String preRegistrationId);

	/**
	 * get Pre Registration
	 * @param preRegistrationId preRegId
	 * @param syncJobId sJobId
	 * @return response DTO
	 */
	public ResponseDTO getPreRegistration(String preRegistrationId, String syncJobId);

}
