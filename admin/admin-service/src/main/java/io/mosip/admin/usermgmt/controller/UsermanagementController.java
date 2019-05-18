/*
 * 
 * 
 * 
 * 
 */
package io.mosip.admin.usermgmt.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.usermgmt.dto.RidVerificationRequestDto;
import io.mosip.admin.usermgmt.dto.RidVerificationResponseDto;
import io.mosip.admin.usermgmt.dto.UserPasswordRequestDto;
import io.mosip.admin.usermgmt.dto.UserPasswordResponseDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.service.UsermanagementService;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

/**
 * Rest Controller for User Registration
 * 
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@RequestMapping("/usermgmt")
@Api(value = "Operation related to User registration", tags = { "user-registration" })
public class UsermanagementController {

	/**
	 * {@link CryptomanagerService} instance
	 */
	@Autowired
	private UsermanagementService userRegistrationService;

	@ResponseFilter
	@PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
	public ResponseWrapper<UserRegistrationResponseDto> register(
			@ApiParam("Basic User Details") @RequestBody @Valid RequestWrapper<UserRegistrationRequestDto> userRegistrationRequestDto) {
		ResponseWrapper<UserRegistrationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userRegistrationService.register(userRegistrationRequestDto.getRequest()));
		return responseWrapper;
	}

	@ResponseFilter
	@PostMapping(value = "/rid", produces = "application/json", consumes = "application/json")
	public ResponseWrapper<RidVerificationResponseDto> ridVerification(@ApiParam("Rid and username details")
			@RequestBody @Valid RequestWrapper<RidVerificationRequestDto> ridRequestDto) {
		ResponseWrapper<RidVerificationResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userRegistrationService.ridVerification(ridRequestDto.getRequest()));
		return responseWrapper;
	}
	
	@ResponseFilter
	@PostMapping(value = "/password", produces = "application/json", consumes = "application/json")
	public ResponseWrapper<UserPasswordResponseDto> addPassword(@ApiParam("Rid and username details")
			@RequestBody @Valid RequestWrapper<UserPasswordRequestDto> request) {
		ResponseWrapper<UserPasswordResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(userRegistrationService.addPassword(request.getRequest()));
		return responseWrapper;
	}
}
