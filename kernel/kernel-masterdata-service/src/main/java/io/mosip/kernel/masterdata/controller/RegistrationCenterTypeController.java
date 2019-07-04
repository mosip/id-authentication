package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.swagger.annotations.Api;

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
	 * @param registrationCenterTypeDto the request dto containing the data of
	 *                                  registration center type to be added.
	 * @return the response dto.
	 */
	@ResponseFilter
	@PostMapping("/registrationcentertypes")
	public ResponseWrapper<CodeAndLanguageCodeID> createRegistrationCenterType(
			@Valid @RequestBody RequestWrapper<RegistrationCenterTypeDto> registrationCenterTypeDto) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterTypeService.createRegistrationCenterType(registrationCenterTypeDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller method for updating a registration center type.
	 * 
	 * @param registrationCenterTypeDto the request dto containing the data of
	 *                                  registration center type to be updated.
	 * @return the response dto.
	 */
	@ResponseFilter
	@PutMapping("/registrationcentertypes")
	public ResponseWrapper<CodeAndLanguageCodeID> updateRegistrationCenterType(
			@Valid @RequestBody RequestWrapper<RegistrationCenterTypeDto> registrationCenterTypeDto) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				registrationCenterTypeService.updateRegistrationCenterType(registrationCenterTypeDto.getRequest()));
		return responseWrapper;
	}

	/**
	 * Controller method for deleting a registration center type.
	 * 
	 * @param code the code of the registration center type that needs to be
	 *             deleted.
	 * @return the response.
	 */
	@ResponseFilter
	@DeleteMapping("/registrationcentertypes/{code}")
	public ResponseWrapper<CodeResponseDto> deleteRegistrationCenterType(@PathVariable("code") String code) {

		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.deleteRegistrationCenterType(code));
		return responseWrapper;
	}
}
