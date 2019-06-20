package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.RegistrationCenterTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
	 * @param registrationCenterTypeDto
	 *            the request dto containing the data of registration center type to
	 *            be updated.
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
	 * @param code
	 *            the code of the registration center type that needs to be deleted.
	 * @return the response.
	 */
	@ResponseFilter
	@DeleteMapping("/registrationcentertypes/{code}")
	public ResponseWrapper<CodeResponseDto> deleteRegistrationCenterType(@PathVariable("code") String code) {

		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.deleteRegistrationCenterType(code));
		return responseWrapper;
	}

	/**
	 * Method to get all the registration Center Types;
	 * 
	 * @return list of registration center types
	 */
	@GetMapping("/registrationcentertypes/all")
	@ResponseFilter
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ApiOperation(value = "Retrieve all the registration center types with additional metadata", notes = "Retrieve all the registration center types with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of registration center types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving registration center types") })
	public ResponseWrapper<PageDto<RegistrationCenterTypeExtnDto>> getAllRegistrationCenterTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<RegistrationCenterTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterTypeService.getAllRegistrationCenterTypes(pageNumber, pageSize,
				sortBy, orderBy.name()));
		return responseWrapper;
	}
}
