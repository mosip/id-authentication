package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for RegistrationCenterType operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "RegistrationCenterType" })
public class RegistrationCenterTypeController {

	/**
	 * Autowired reference for {@link RegistrationCenterTypeService}.
	 */
	@Autowired
	RegistrationCenterTypeService registrationCenterTypeService;

	/**
	 * Controller method for creating a registration center type.
	 * 
	 * @param registrationCenterTypeDto
	 *            the request dto containing the data of registration center type to
	 *            be added.
	 * @return the response dto.
	 */
	@PostMapping("/v1.0/registrationcentertypes")
	public ResponseEntity<CodeAndLanguageCodeID> createRegistrationCenterType(
			@Valid @RequestBody RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto) {
		return new ResponseEntity<>(
				registrationCenterTypeService.createRegistrationCenterType(registrationCenterTypeDto),
				HttpStatus.CREATED);
	}

	/**
	 * Controller method for updating a registration center type.
	 * 
	 * @param registrationCenterTypeDto
	 * @return
	 */
	@PutMapping("/v1.0/registrationcentertypes")
	public ResponseEntity<CodeAndLanguageCodeID> updateRegistrationCenterType(
			@Valid @RequestBody RequestDto<RegistrationCenterTypeDto> registrationCenterTypeDto) {
		return new ResponseEntity<>(
				registrationCenterTypeService.updateRegistrationCenterType(registrationCenterTypeDto),
				HttpStatus.CREATED);

	}

	/**
	 * Controller method for deleting a registration center type.
	 * 
	 * @param code
	 * @return
	 */
	@DeleteMapping("/v1.0/registrationcentertypes/{code}")
	@ApiOperation(value = "Service to delete registration center type.", notes = "Delete registration center type and return composite id", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When registration center successfully deleted.", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When input request has null or invalid values."),
			@ApiResponse(code = 404, message = "When no registration center found."),
			@ApiResponse(code = 500, message = "Error occured while deleting registration center.") })
	public ResponseEntity<CodeResponseDto> deleteRegistrationCenterType(@PathVariable("code") String code) {
		return new ResponseEntity<>(registrationCenterTypeService.deleteRegistrationCenterType(code), HttpStatus.OK);
	}
}
