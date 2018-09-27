package org.mosip.registration.controller;

import java.util.Map;

import org.mosip.registration.constants.StatusCodes;
import org.mosip.registration.dto.UserDto;
import org.mosip.registration.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/pre-registration")
@Api(tags = "User Management")
@CrossOrigin("*")
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;

	@GetMapping(path = "/login")
	@ApiOperation(value = "User login for OTP generation", response = StatusCodes.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User OTP sucessfully generated"),
			@ApiResponse(code = 406, message = "User OTP generation failed") })
	public ResponseEntity<Map<String, StatusCodes>> userLogin(@RequestParam(value = "userName", required = true) String userName) {
		Map<String,StatusCodes> response = userManagementService.userLogin(userName);
		HttpStatus status = response.get("ok") != null ? HttpStatus.OK : HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity.status(status).body(response);
	}

	@PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "User OTP Validation", response = StatusCodes.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User OTP sucessfully validated"),
			@ApiResponse(code = 406, message = "User OTP validation failed") })
	public ResponseEntity<Map<String, StatusCodes>> userValidation(@RequestParam(value = "userName", required = true) String userName,
			@RequestBody(required = true) String otp) {
		Map<String,StatusCodes> response = userManagementService.userValidation(userName, otp);
		HttpStatus status = response.get("ok") != null ? HttpStatus.OK : HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity.status(status).body(response);
	}

	@PutMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update user data", response = StatusCodes.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "User data updated sucessfully"),
			@ApiResponse(code = 406, message = "User not found") })
	// pass the json with two fields. existing and new one.
	public ResponseEntity<Map<String, StatusCodes>> userUpdation(@RequestParam(value = "userName", required = true) String userName,
			@RequestBody(required = true) UserDto userdto){
		Map<String,StatusCodes> response = userManagementService.userUpdation(userName, userdto);
		HttpStatus status = response.get("ok") != null ? HttpStatus.OK : HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity.status(status).body(response);
	}
}
