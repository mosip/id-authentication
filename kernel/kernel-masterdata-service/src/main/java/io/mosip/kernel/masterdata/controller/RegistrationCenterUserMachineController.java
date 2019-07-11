package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.RegCenterMachineUserReqDto;
import io.mosip.kernel.masterdata.dto.RegCenterMachineUserResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller with api for crud operation related to
 * RegistrationCenterUserMachine Mappings
 * 
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 * @see RegistrationCenterMachineUserID
 * @see RegistrationCenterUserMachineMappingDto
 */
@CrossOrigin
@RestController
@Api(tags = { "RegistrationCenterUserMachine" })
public class RegistrationCenterUserMachineController {

	/**
	 * {@link RegistrationCenterMachineUserService} instance
	 */
	@Autowired
	RegistrationCenterMachineUserService registrationCenterMachineUserService;

	/**
	 * Create a mapping of registration center,user,and machine
	 * 
	 * @param registrationCenterUserMachineMappingDto {@link RegistrationCenterUserMachineMappingDto}
	 *                                                request
	 * @return {@link RegistrationCenterMachineUserID} as response
	 */
	@ResponseFilter
	@ApiOperation(value = "Create a mapping of registration center,user,and machine")
	@PostMapping("/registrationmachineusermappings")
	public ResponseWrapper<RegistrationCenterMachineUserID> createRegistrationCentersMachineUserMapping(
			@ApiParam("Registration center id,user id and ,machine id with metadata") @RequestBody @Valid RequestWrapper<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto) {

		ResponseWrapper<RegistrationCenterMachineUserID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineUserService
				.createRegistrationCentersMachineUserMapping(registrationCenterUserMachineMappingDto));
		return responseWrapper;
	}

	/**
	 * Delete a mapping of registration center,user,and machine
	 * 
	 * @param regCenterId input from user
	 * @param machineId   input from user
	 * @param userId      input from user
	 * @return {@link RegistrationCenterMachineUserID} as response
	 */
	@ResponseFilter
	@ApiOperation(value = "Delete the mapping of registration center and user and machine")
	@DeleteMapping("/registrationmachineusermappings/{regCenterId}/{machineId}/{userId}")
	public ResponseWrapper<RegistrationCenterMachineUserID> deleteRegistrationCenterUserMachineMapping(
			@ApiParam("Registration center id to be deleted") @PathVariable String regCenterId,
			@ApiParam("Machine id to be deleted") @PathVariable String machineId,
			@ApiParam("User id to be deleted") @PathVariable String userId) {

		ResponseWrapper<RegistrationCenterMachineUserID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineUserService
				.deleteRegistrationCentersMachineUserMapping(regCenterId, machineId, userId));
		return responseWrapper;
	}

	/**
	 * Create or update a mapping of registration center,user,and machine
	 * 
	 * @param regCenterMachineUserReqDto {@link RegCenterMachineUserReqDto} request
	 * @return {@link RegCenterMachineUserResponseDto} as response
	 */
	@ResponseFilter
	@ApiOperation(value = "Create or update a mapping of registration center,user,and machine")
	@PutMapping("/registrationmachineusermappings")
	public ResponseWrapper<RegCenterMachineUserResponseDto> createOrUpdateRegistrationCentersMachineUserMapping(
			@ApiParam("Registration center id,user id and ,machine id with metadata") @RequestBody @Valid RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> regCenterMachineUserReqDto) {
		
		ResponseWrapper<RegCenterMachineUserResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterMachineUserService
				.createOrUpdateRegistrationCentersMachineUserMapping(regCenterMachineUserReqDto));
		return responseWrapper;
	}

}
