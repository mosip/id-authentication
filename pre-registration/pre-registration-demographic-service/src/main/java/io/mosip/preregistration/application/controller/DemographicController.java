/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.auth.adapter.AuthUserDetails;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.application.dto.DeletePreRegistartionDTO;
import io.mosip.preregistration.application.dto.DemographicRequestDTO;
import io.mosip.preregistration.application.dto.PreRegistrationViewDTO;
import io.mosip.preregistration.application.service.DemographicService;
import io.mosip.preregistration.core.common.dto.DemographicResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegistartionStatusDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides different API's to perform operations on
 * pre-registration.
 * 
 * @author Rajath KR
 * @author Sanober Noor
 * @author Tapaswini Behera
 * @author Jagadishwari S
 * @author Ravi C Balaji
 * @since 1.0.0
 */

@RestController
@RequestMapping("/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class DemographicController {

	/** Autowired reference for {@link #DemographicService}. */
	@Autowired
	private DemographicService preRegistrationService;

	private Logger log = LoggerConfiguration.logConfig(DemographicController.class);

	/**
	 * Post API to create a pre-registation application.
	 *
	 * @param jsonObject
	 *            the json object
	 * @return List of response dto containing pre-id and group-id
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@PostMapping(path = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create form data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully Created"),
			@ApiResponse(code = 400, message = "Unable to create the demographic data") })
	public ResponseEntity<MainListResponseDTO<DemographicResponseDTO>> register(
			@RequestBody(required = true) MainRequestDTO<DemographicRequestDTO> jsonObject) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for registration with json object" + jsonObject);
//		AuthUserDetails authUserDetails = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    	System.out.println(authUserDetails.getUserId()); 
// 
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.addPreRegistration(jsonObject));
	}

	/**
	 * Get API to fetch all the Pre-registration data for a pre-id.
	 *
	 * @param preRegId
	 *            the pre reg id
	 * @return the application data for a pre-id
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@GetMapping(path = "/applications/details", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the demographic data") })
	public ResponseEntity<MainListResponseDTO<DemographicResponseDTO>> getApplication(
			@RequestParam(value = "pre_registration_id", required = true) String preRegistraionId) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for fetching all demographic data with preregistartionId"
						+ preRegistraionId);
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getDemographicData(preRegistraionId));
	}

	/**
	 * Put API to update the status of the application.
	 *
	 * @param preRegId
	 *            the pre reg id
	 * @param status
	 *            the status
	 * @return the updation status of application for a pre-id
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@PutMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update Pre-Registartion status")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Pre-Registration Status successfully updated"),
			@ApiResponse(code = 400, message = "Unable to update the Pre-Registration") })
	public ResponseEntity<MainResponseDTO<String>> updateApplicationStatus(
			@RequestParam(value = "pre_registration_id", required = true) String preRegId,
			@RequestParam(value = "status_code", required = true) String status) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for fetching all demographic data with preRegId " + preRegId
						+ " and status " + status);
		return ResponseEntity.status(HttpStatus.OK)
				.body(preRegistrationService.updatePreRegistrationStatus(preRegId, status));
	}

	/**
	 * Post api to fetch all the applications created by user.
	 *
	 * @param userId
	 *            the user id
	 * @return List of applications created by User
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@GetMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch all the applications created by user")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch applications ") })
	public ResponseEntity<MainListResponseDTO<PreRegistrationViewDTO>> getAllApplications(
			@RequestParam(value = "user_id", required = true) String userId, HttpServletRequest res) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for fetching all applications with userId " + userId + " Header "
						+ res.getHeader("Authorization"));
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getAllApplicationDetails(userId));
	}

	/**
	 * Post API to fetch the status of a application.
	 *
	 * @param preId
	 *            the pre id
	 * @return status of application
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@GetMapping(path = "/applications/status", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch the status of a application")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All applications status fetched successfully"),
			@ApiResponse(code = 400, message = "Unable to fetch application status ") })
	public ResponseEntity<MainListResponseDTO<PreRegistartionStatusDTO>> getApplicationStatus(
			@RequestParam(value = "pre_registration_id", required = true) String preId) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for fetching all applicationStatus with preId " + preId);
		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.getApplicationStatus(preId));
	}

	/**
	 * Delete API to delete the Individual applicant and documents associated with
	 * the PreId.
	 *
	 * @param preId
	 *            the pre id
	 * @return the deletion status of application for a pre-id
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@DeleteMapping(path = "/applications", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Discard individual")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Deletion of individual is successfully"),
			@ApiResponse(code = 400, message = "Unable to delete individual") })
	public ResponseEntity<MainListResponseDTO<DeletePreRegistartionDTO>> discardIndividual(
			@RequestParam(value = "pre_registration_id") String preId) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for deletion of individual with preId " + preId);

		return ResponseEntity.status(HttpStatus.OK).body(preRegistrationService.deleteIndividual(preId));
	}

	/**
	 * Get API to fetch all the pre-ids within from-date and to-date range.
	 *
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @return the pre-ids for date range
	 */
	@PreAuthorize("hasAnyRole('individual')")
	@GetMapping(path = "/applications/byDateTime", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion data By Date And Time")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Demographic data successfully retrieved"),
			@ApiResponse(code = 400, message = "Unable to get the Pre-Registration data") })
	public ResponseEntity<MainListResponseDTO<String>> getApplicationByDate(
			@RequestParam(value = "from_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
			@RequestParam(value = "to_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
		log.info("sessionId", "idType", "id",
				"In pre-registration controller for fetching all application from " + fromDate + " to " + toDate);
		return ResponseEntity.status(HttpStatus.OK)
				.body(preRegistrationService.getPreRegistrationByDate(fromDate, toDate));
	}
	
	@PreAuthorize("hasAnyRole('individual')")
	@PostMapping(path = "/applications/updatedTime", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Updated Date Time for List of Pre-Registration Id")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Updated Date Time successfully fetched for list of pre-registration ids"),
			@ApiResponse(code = 400, message = "Unable to the Updated Date Time") })
	public ResponseEntity<MainResponseDTO<Map<String, String>>> getUpdatedDateTimeByPreIds(
			@RequestBody MainRequestDTO<PreRegIdsByRegCenterIdDTO> mainRequestDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(
				preRegistrationService.getUpdatedDateTimeForPreIds(mainRequestDTO.getRequest()));
	}

}
