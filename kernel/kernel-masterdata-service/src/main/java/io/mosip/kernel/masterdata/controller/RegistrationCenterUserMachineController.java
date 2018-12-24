package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService;
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
@Api(tags = { "RegistrationCenterUserMachine" })
public class RegistrationCenterUserMachineController {

	/**
	 * {@link RegistrationCenterMachineUserService} instance
	 */
	@Autowired
	RegistrationCenterMachineUserService registrationCenterMachineUserService;

	
	@PostMapping("/v1.0/registrationmachineusermappings")
	public ResponseEntity<RegistrationCenterMachineUserID> createRegistrationCentersMachineUserMapping(
			@RequestBody @Valid RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto) {
		return new ResponseEntity<>(registrationCenterMachineUserService.createRegistrationCentersMachineUserMapping(registrationCenterUserMachineMappingDto), HttpStatus.CREATED);
	}

}
