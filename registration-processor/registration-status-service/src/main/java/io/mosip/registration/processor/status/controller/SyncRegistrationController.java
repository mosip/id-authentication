/*package io.mosip.registration.processor.status.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

*//**
 * The Class SyncRegistrationController.
 *
 * @author M1047487
 *//*
@RestController
@RequestMapping("/v0.1/registration-processor/registration-status")
@Api(tags = "Sync Registration")
public class SyncRegistrationController {

	@Autowired
	SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;

	*//**
	 * Instantiates a new sync registration controller.
	 *//*
	public SyncRegistrationController() {
		super();
	}

	*//**
	 * Sync registration controller.
	 *
	 * @param syncRegistrationDto
	 *            the sync registration dto
	 * @return the response entity
	 *//*
	@PostMapping(path = "/sync", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the synchronizing registration entity", response = RegistrationStatusCode.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Synchronizing Registration Entity successfully fetched") })
	public ResponseEntity<List<SyncRegistrationDto>> syncRegistrationController(
			@RequestBody(required = true) List<SyncRegistrationDto> syncRegistrationDto) {
		List<SyncRegistrationDto> syncRegistrationDtoResponse = syncRegistrationService.sync(syncRegistrationDto);
		return ResponseEntity.status(HttpStatus.OK).body(syncRegistrationDtoResponse);
	}

}
*/