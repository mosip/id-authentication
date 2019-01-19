package io.mosip.kernel.masterdata.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
/**
 * Service for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public interface RegistrationCenterMachineUserHistoryService {

	/**
	 * This method will call repository for fetching data
	 * 
	 * @param langCode
	 *            language code provided by user
	 * @param effectiveTimestamp
	 *            effective time stamp provided by user
	 * @param registrationCenterId
	 *            registration center id provided by user
	 * @param machineId
	 *            machine id provided by user
	 * @return {@link RegistrationCenterUserMachineMappingHistoryResponseDto} response
	 */
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			String langCode, String effectiveTimestamp,
			String registrationCenterId, String machineId);

}
