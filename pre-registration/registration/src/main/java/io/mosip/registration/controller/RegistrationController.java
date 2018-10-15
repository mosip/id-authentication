package io.mosip.registration.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.code.RegistrationCode;
import io.mosip.registration.core.generator.MosipGroupIdGenerator;
import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.dto.ViewRegistrationResponseDto;
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


	/**
	 * Field for {@link #ViewRegistrationService}
	 */
	@Autowired
	private RegistrationService<String, RegistrationDto> registrationService;
	
	@Autowired
	private MosipGroupIdGenerator<String> groupIdGenerator;
	
	private String groupId;
	
	private boolean isNewApplication=true;
	
	/**
	 * 
	 * @param list of application forms
	 * @return List of response dto containing pre-id and group-id
	 */

	@PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save form data", response = RegistrationCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully saved"),
			@ApiResponse(code = 400, message = "Unable to save the Registration Entity") })
	public ResponseEntity<List<ResponseDto>> register(@RequestBody(required = true) ApplicationDto applications) {
		List<ResponseDto> response= new ArrayList<>();
		int noOfApplications=applications.getApplications().size();
		for (int i = 0; i < noOfApplications; i++) {
			if (!applications.getApplications().get(i).getGroupId().isEmpty()) {
				groupId = applications.getApplications().get(i).getGroupId();
				isNewApplication = false;
				break;
			}
		}
		if(isNewApplication) {
			groupId= groupIdGenerator.generateGroupId();
		}
		for (RegistrationDto registartion : applications.getApplications()) {
			response.add(registrationService.addRegistration(registartion,groupId));
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Post api to fetch all the applications created by user
	 * 
	 * @return List of applications created by User
	 */
	@PostMapping(path = "/Applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "fetch all the applications created by user")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch applications ") })
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
	@PostMapping(path = "/ApplicationStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "fetch the status of a application", response = RegistrationCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications status fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch application status ") })
	public ResponseEntity<Map<String, String>> getApplicationStatus(
			@RequestParam(value = "groupId", required = true) String groupId)

	{
		Map<String, String> response = registrationService.getApplicationStatus(groupId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}
	
	/**
	 * Delete api to delete the Individual applicant and documents associated with it
	 * 
	 */
	
    @DeleteMapping(path = "/discard", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Discard individual")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of individual is successfully"),
			@ApiResponse(code = 400, message = "Unable to delete individual") })
	public  ResponseEntity<List<ResponseDto>> discardIndividual(@RequestParam(value = "groupId") String groupId,
			@RequestParam(value = "preregIds") List<String> preregIds) {
		
		List<ResponseDto> response=registrationService.deleteIndividual(groupId, preregIds);
		return ResponseEntity.status(HttpStatus.OK).body(response);
		
	}

	/**
	 * Delete api to delete the Group applicants and documents associated with it
	 * 
	 */

	 @DeleteMapping(path = "/discardGroup", produces = MediaType.APPLICATION_JSON_VALUE)
	 @ApiOperation(value = "delete the Group applicants")
	 @ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of group is successfully"),
				@ApiResponse(code = 400, message = "Unable to delete group") })
	public ResponseEntity<List<ResponseDto>> discardGroup(@RequestParam(value = "groupId") String groupId) {
		List<ResponseDto> response=registrationService.deleteGroup(groupId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
