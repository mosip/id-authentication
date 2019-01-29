package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegCenterMachineUserReqDto;
import io.mosip.kernel.masterdata.dto.RegCenterMachineUserResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
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
	 * @param registrationCenterUserMachineMappingDto
	 *            {@link RegistrationCenterUserMachineMappingDto} request
	 * @return {@link RegistrationCenterMachineUserID} as response
	 */
	@ApiOperation(value = "Create a mapping of registration center,user,and machine", response = RegistrationCenterMachineUserID.class)
	@PostMapping("/v1.0/registrationmachineusermappings")
	public ResponseEntity<RegistrationCenterMachineUserID> createRegistrationCentersMachineUserMapping(
			@ApiParam("Registration center id,user id and ,machine id with metadata") @RequestBody @Valid RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto) {
		return new ResponseEntity<>(registrationCenterMachineUserService.createRegistrationCentersMachineUserMapping(
				registrationCenterUserMachineMappingDto), HttpStatus.CREATED);
	}

	/**
	 * Delete a mapping of registration center,user,and machine
	 * 
	 * @param regCenterId
	 *            input from user
	 * @param machineId
	 *            input from user
	 * @param userId
	 *            input from user
	 * @return {@link RegistrationCenterMachineUserID} as response
	 */
	@ApiOperation(value = "Delete the mapping of registration center and user and machine", response = RegistrationCenterMachineUserID.class)
	@DeleteMapping("/v1.0/registrationmachineusermappings/{regCenterId}/{machineId}/{userId}")
	public ResponseEntity<RegistrationCenterMachineUserID> deleteRegistrationCenterUserMachineMapping(
			@ApiParam("Registration center id to be deleted") @PathVariable String regCenterId,
			@ApiParam("Machine id to be deleted") @PathVariable String machineId,
			@ApiParam("User id to be deleted") @PathVariable String userId) {
		return new ResponseEntity<>(registrationCenterMachineUserService
				.deleteRegistrationCentersMachineUserMapping(regCenterId, machineId, userId), HttpStatus.OK);
	}

	/**
	 * Create or update a mapping of registration center,user,and machine
	 * 
	 * @param registrationCenterUserMachineMappingDto
	 *            {@link RegistrationCenterUserMachineMappingDto} request
	 * @return {@link RegistrationCenterMachineUserID} as response
	 */
	@ApiOperation(value = "Create or update a mapping of registration center,user,and machine", response = RegistrationCenterMachineUserID.class)
	@PutMapping("/v1.0/registrationmachineusermappings")
	public ResponseEntity<RegCenterMachineUserResponseDto> createOrUpdateRegistrationCentersMachineUserMapping(
			@ApiParam("Registration center id,user id and ,machine id with metadata") @RequestBody @Valid RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> regCenterMachineUserReqDto) {
		return new ResponseEntity<>(registrationCenterMachineUserService
				.createOrUpdateRegistrationCentersMachineUserMapping(regCenterMachineUserReqDto), HttpStatus.OK);
	}

}
