package io.mosip.preregistration.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.application.dto.CreatePreRegistrationDTO;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.dto.ResponseDTO;
import io.mosip.preregistration.application.dto.UpdateResponseDTO;
import io.mosip.preregistration.application.service.DemographicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides the
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */
 
@RestController
@RequestMapping("/v0.1/pre-registration/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class DemographicController {

	/**
	 * Field for {@link #ViewRegistrationService}
	 */
	@Autowired
	private DemographicService preRegistrationService;

	/**
	 * 
	 * @param list of application forms
	 * @return List of response dto containing pre-id and group-id
	 */

	@PostMapping(path = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create form data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully Created"),
			@ApiResponse(code = 400, message = "Unable to create the demographic data") })
	public ResponseEntity<ResponseDTO<CreatePreRegistrationDTO>> register(
			@RequestBody(required = true) DemographicRequestDTO<CreatePreRegistrationDTO> jsonObject) {
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.addPreRegistration(jsonObject));
	}

	/**
	 * @param preRegId
	 * @return
	 */
	@GetMapping(path = "/applicationData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the demographic data") })
	public ResponseEntity<ResponseDTO<CreatePreRegistrationDTO>> getApplication(
			@RequestParam(value = "preRegId", required = true) String preRegId) {
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getDemographicData(preRegId));
	}

	/**
	 * @param preRegId
	 * @param status
	 * @return
	 */
	@PutMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Pre-Registartion status")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Pre-Registration Status successfully updated"),
			@ApiResponse(code = 400, message = "Unable to update the Pre-Registration") })
	public ResponseEntity<UpdateResponseDTO<String>> updateApplicationStatus(
			@RequestParam(value = "preRegId", required = true) String preRegId,
			@RequestParam(value = "status", required = true) String status) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(preRegistrationService.updatePreRegistrationStatus(preRegId, status));
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
	public ResponseEntity<ResponseDTO<PreRegistrationViewDTO>> getAllApplications(
			@RequestParam(value = "userId", required = true) String userId) {
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getAllApplicationDetails(userId));
	}

	/**
	 * Post API to fetch the status of a application
	 * 
	 * @return status of application
	 */
	@GetMapping(path = "/applicationStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch the status of a application")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications status fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch application status ") })
	public ResponseEntity<ResponseDTO<PreRegistartionStatusDTO>> getApplicationStatus(
			@RequestParam(value = "preId", required = true) String preId) {
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getApplicationStatus(preId));
	}

	/**
	 * Delete API to delete the Individual applicant and documents associated with
	 * the PreId
	 * 
	 */
	@DeleteMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Discard individual")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of individual is successfully"),
			@ApiResponse(code = 400, message = "Unable to delete individual") })
	public ResponseEntity<ResponseDTO<DeletePreRegistartionDTO>> discardIndividual(
			@RequestParam(value = "preId") String preId) {
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.deleteIndividual(preId));
	}

	/**
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@GetMapping(path = "/applicationDataByDateTime", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion data By Date And Time")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the Pre-Registration data") })
	public ResponseEntity<ResponseDTO<String>> getApplicationByDate(@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(preRegistrationService.getPreRegistrationByDate(fromDate, toDate));
	}

}
