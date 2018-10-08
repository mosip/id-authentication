package io.mosip.registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.code.RegistrationCode;
import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.RegistrationDto;
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
	

	@PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save form data", response = RegistrationCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully saved"),
			@ApiResponse(code = 400, message = "Unable to save the Registration Entity") })
	public ResponseEntity<List<RegistrationDto>> register(@RequestBody(required = true) ApplicationDto applications) {
		applicationHelper.Helper(applications);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
}
