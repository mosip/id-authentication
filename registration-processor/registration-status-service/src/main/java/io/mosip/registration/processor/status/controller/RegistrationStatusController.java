package io.mosip.registration.processor.status.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * The Class RegistrationStatusController.
 */
@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/registration-status")
@Api(tags = "Registration Status")
public class RegistrationStatusController {

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;

	/**
	 * Search.
	 *
	 * @param registrationIds
	 *            the registration ids
	 * @return the response entity
	 */
	@GetMapping(path = "/registrationstatus", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the registration entity", response = RegistrationExternalStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
	public ResponseEntity<List<RegistrationStatusDto>> search(
			@RequestParam(value = "registrationIds", required = true) String registrationIds) {
		List<RegistrationStatusDto> registrations = registrationStatusService.getByIds(registrationIds);
		return ResponseEntity.status(HttpStatus.OK).body(registrations);
	}

	/**
	 * Sync registration ids.
	 *
	 * @param syncRegistrationDto
	 *            the sync registration dto
	 * @return the response entity
	 */
	@PostMapping(path = "/sync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the synchronizing registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Synchronizing Registration Entity successfully fetched")})
	public ResponseEntity<Object> syncRegistrationController(
			@RequestBody(required = true) List<SyncRegistrationDto> syncRegistrationList) {
		List<SyncResponseDto> syncResponseDtoList = syncRegistrationService.sync(syncRegistrationList);
		if(!syncResponseDtoList.isEmpty()) {
			return ResponseEntity.ok().body(syncResponseDtoList);
		}else {
			System.out.println("Calling else block");
			return ResponseEntity.badRequest().body(syncResponseDtoList);
		}
	}
}
