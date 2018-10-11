package io.mosip.registration.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.code.RegistrationCode;
import io.mosip.registration.dto.ApplicationDto;

import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.dto.ViewRegistrationResponseDto;
import io.mosip.registration.helper.ApplicationHelper;
import io.mosip.registration.service.RegistrationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/pre-registration/registration/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class RegistrationController {

	@Autowired
	ApplicationHelper applicationHelper;
	/**
	 * Field for {@link #ViewRegistrationService}
	 */
	@Autowired
	private RegistrationService<?,?> registrationService;

	@PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save form data", response = RegistrationCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully saved"),
			@ApiResponse(code = 400, message = "Unable to save the Registration Entity") })
	public ResponseEntity<List<ResponseDto>> register(@RequestBody(required = true) ApplicationDto applications) {
		List<ResponseDto> response = applicationHelper.Helper(applications);
		return ResponseEntity.status(HttpStatus.OK).body(response);
		// applicationHelper.test("479548729071");
		// return null;
	}

	/**
	 * Post api to fetch all the applications created by user
	 * 
	 * @return List of applications created by User
	 */
	@PostMapping(path = "/Applications")
	public ResponseEntity<List<ViewRegistrationResponseDto>> getAllApplications(
			@RequestParam(value = "userId", required = true) String userId)

	{
		List<ViewRegistrationResponseDto> response = registrationService.getApplicationDetails(userId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * Post api to fetch the status of a application
	 * 
	 * @return status of application
	 */
	@PostMapping(path = "/ApplicationStatus")
	public ResponseEntity<Map<String, String>> getApplicationStatus(
			@RequestParam(value = "groupId", required = true) String groupId)

	{
		Map<String, String> response = registrationService.getApplicationStatus(groupId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

}
