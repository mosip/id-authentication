package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService;

/**
 * * Controller with api for crud operation related to
 * RegistrationCenterUserMachine Mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
public class RegistrationCenterUserMachineHistoryController {

	/**
	 * {@link RegistrationCenterMachineUserHistoryService} instance
	 */
	@Autowired
	RegistrationCenterMachineUserHistoryService registrationCenterMachineUserHistoryService;

	/**
	 * Get api to fetch user machine mappings based on user inputs
	 * 
	 * @param effectiveTimestamp
	 *            effective timestamp provided by user
	 * @param registrationCenterId
	 *            registration center provided by user
	 * @param machineId
	 *            machine id provided by user
	 * @param userId
	 *            user id provided by user
	 * @return {@link RegistrationCenterUserMachineMappingHistoryResponseDto} based on
	 *         user inputs
	 */
	@GetMapping("/v1.0/getregistrationmachineusermappinghistory/{effdtimes}/{registrationcenterid}/{machineid}/{userid}")
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			@PathVariable("effdtimes") String effectiveTimestamp,
			@PathVariable("registrationcenterid") String registrationCenterId,
			@PathVariable("machineid") String machineId,
			@PathVariable("userid") String userId) {
		return registrationCenterMachineUserHistoryService
				.getRegistrationCentersMachineUserMapping(effectiveTimestamp,
						registrationCenterId, machineId, userId);
	}

}
