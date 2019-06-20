package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService;
import io.swagger.annotations.Api;

/**
 * * Controller with api for crud operation related to
 * RegistrationCenterUserMachine Mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "RegistrationCenterUserMachineHistory" })
public class RegistrationCenterUserMachineHistoryController {

	/**
	 * {@link RegistrationCenterMachineUserHistoryService} instance
	 */
	@Autowired
	RegistrationCenterMachineUserHistoryService registrationCenterMachineUserHistoryService;

	/**
	 * Get api to fetch user machine mappings based on user inputs
	 * 
	 * @param effectiveTimestamp   effective timestamp provided by user
	 * @param registrationCenterId registration center provided by user
	 * @param machineId            machine id provided by user
	 * @param userId               user id provided by user
	 * @return {@link RegistrationCenterUserMachineMappingHistoryResponseDto} based
	 *         on user inputs
	 */
	//@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
	@ResponseFilter
	@GetMapping("/getregistrationmachineusermappinghistory/{effdtimes}/{registrationcenterid}/{machineid}/{userid}")
	public ResponseWrapper<RegistrationCenterUserMachineMappingHistoryResponseDto> getRegistrationCentersMachineUserMapping(
			@PathVariable("effdtimes") String effectiveTimestamp,
			@PathVariable("registrationcenterid") String registrationCenterId,
			@PathVariable("machineid") String machineId, @PathVariable("userid") String userId) {

		ResponseWrapper<RegistrationCenterUserMachineMappingHistoryResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineUserHistoryService
				.getRegistrationCentersMachineUserMapping(effectiveTimestamp, registrationCenterId, machineId, userId));
		return responseWrapper;
	}

}
