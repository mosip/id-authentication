package io.mosip.kernel.syncdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.swagger.annotations.ApiOperation;
/**
 * API class handles fetching users based on registration center id
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/v1.0/registrationcenteruser")
public class RegistrationCenterUserController {
    /**
     * Service Instance
     */
	@Autowired
	RegistrationCenterUserService registrationCenterUserService;
	
	@ApiOperation(value = "API to fetch users based on registration center id")
	@GetMapping("/{registrationuserid}")
	public RegistrationCenterUserResponseDto getUsersBasedOnRegCenter(@PathVariable(value="registrationuserid") String regCenterId) {
		return registrationCenterUserService.getUsersBasedOnRegistrationCenterId(regCenterId);
		
	}
}
