package io.mosip.preregistration.application.controller;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.application.dto.CreateDto;
import io.mosip.preregistration.application.dto.DeleteDto;
import io.mosip.preregistration.application.dto.ExceptionInfoDto;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.StatusDto;
import io.mosip.preregistration.application.dto.ViewDto;
import io.mosip.preregistration.application.service.PreRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Registration controller
 * 
 * @author M1037717
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class PreRegistrationController {

	/**
	 * Field for {@link #ViewRegistrationService}
	 */
	@Autowired
	private PreRegistrationService preRegistrationService;

	/**
	 * 
	 * @param list
	 *            of application forms
	 * @return List of response dto containing pre-id and group-id
	 */

	@PostMapping(path = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create form data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Pre-Registration Entity successfully Created"),
			@ApiResponse(code = 400, message = "Unable to create the Pre-Registration Entity") })
	public ResponseEntity<ResponseDto<CreateDto>> register(@RequestBody(required = true) JSONObject jsonObject) {
		ResponseDto<CreateDto> response = preRegistrationService.addRegistration(jsonObject.toJSONString());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping(path = "/applicationData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Pre-Registration Entity successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the Pre-Registration data") })
	public ResponseEntity<ResponseDto<CreateDto>> getApplication(
			@RequestParam(value = "preRegId", required = true) String preRegId) {
		ResponseDto<CreateDto> response = preRegistrationService.getPreRegistration(preRegId);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Post api to fetch all the applications created by user
	 * 
	 * @return List of applications created by User
	 */
	@GetMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch all the applications created by user")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch applications ") })
	public ResponseEntity<ResponseDto<ViewDto>> getAllApplications(
			@RequestParam(value = "userId", required = true) String userId)

	{
		ResponseDto<ViewDto> response = preRegistrationService.getApplicationDetails(userId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * Post api to fetch the status of a application
	 * 
	 * @return status of application
	 */
	@GetMapping(path = "/applicationStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch the status of a application")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications status fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch application status ") })
	public ResponseEntity<ResponseDto<StatusDto>> getApplicationStatus(
			@RequestParam(value = "preId", required = true) String preId)

	{
		ResponseDto<StatusDto> response = preRegistrationService.getApplicationStatus(preId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * Delete api to delete the Individual applicant and documents associated with
	 * it
	 * 
	 */

	@SuppressWarnings("rawtypes")
	@DeleteMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Discard individual")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of individual is successfully"),
			@ApiResponse(code = 400, message = "Unable to delete individual") })
	public ResponseEntity<ResponseDto<DeleteDto>> discardIndividual(@RequestParam(value = "preId") String preId) {

		ResponseDto<DeleteDto> response = preRegistrationService.deleteIndividual(preId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

}
