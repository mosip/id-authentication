package io.mosip.registration.processor.status.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.dto.RegistrationExternalStatusDto;
import io.mosip.registration.processor.status.service.RegistrationExternalStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/registration-external-status")
@Api(tags = "Status Handler")
public class RegistrationExternalStatusController {

	@Autowired
	RegistrationExternalStatusService<String, RegistrationExternalStatusDto> registrationExternalStatusService;

	@GetMapping(path = "/registrationexternalstatus", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get the registration entity", response = RegistrationExternalStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully fetched"),
			@ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
	public ResponseEntity<List<RegistrationExternalStatusDto>> search(
			@RequestParam(value = "registrationIds", required = true) String registrationIds) {
		List<RegistrationExternalStatusDto> registrations = registrationExternalStatusService.getByIds(registrationIds);
		return ResponseEntity.status(HttpStatus.OK).body(registrations);
	}
}