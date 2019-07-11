package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import io.mosip.kernel.masterdata.dto.GenderTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.GenderTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.GenderExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.GenderTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class for fetching gender data from DB
 * 
 * @author Urvil Joshi
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@CrossOrigin
@RestController
@Api(value = "Operation related to Gender Type", tags = { "GenderType" })
public class GenderTypeController {
	@Autowired
	private GenderTypeService genderTypeService;

	/**
	 * Get API to fetch all gender types
	 * 
	 * @return list of all gender types
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION', 'REGISTRATION_ADMIN', 'REGISTRATION_SUPERVISOR', 'REGISTRATION_OFFICER', 'REGISTRATION_PROCESSOR','ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping("/gendertypes")
	public ResponseWrapper<GenderTypeResponseDto> getAllGenderType() {
		ResponseWrapper<GenderTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.getAllGenderTypes());
		return responseWrapper;
	}

	/**
	 * Get API to fetch all gender types for a particular language code
	 * 
	 * @param langCode
	 *            the language code whose gender is to be returned
	 * @return list of all gender types for the given language code
	 */
	@PreAuthorize("hasAnyRole('INDIVIDUAL','ID_AUTHENTICATION')")
	@ResponseFilter
	@GetMapping(value = "/gendertypes/{langcode}")
	public ResponseWrapper<GenderTypeResponseDto> getGenderBylangCode(@PathVariable("langcode") String langCode) {

		ResponseWrapper<GenderTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.getGenderTypeByLangCode(langCode));
		return responseWrapper;
	}

	/**
	 * Post API to enter a new Gender Type Data
	 * 
	 * @param gender
	 *            input dto to enter a new gender data
	 * @return primary key of entered row of gender
	 */
	@ResponseFilter
	@PostMapping("/gendertypes")
	public ResponseWrapper<CodeAndLanguageCodeID> saveGenderType(
			@Valid @RequestBody RequestWrapper<GenderTypeDto> gender) {
		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.saveGenderType(gender.getRequest()));
		return responseWrapper;

	}

	/**
	 * Update a Gender Type
	 * 
	 * @param gender
	 *            input dto to update a gender data
	 * @return key of updated row
	 */
	@ResponseFilter
	@ApiOperation(value = "Update Gender Type")
	@PutMapping("/gendertypes")
	public ResponseWrapper<CodeAndLanguageCodeID> updateGenderType(
			@ApiParam("Data to update with metadata") @Valid @RequestBody RequestWrapper<GenderTypeDto> gender) {
		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.updateGenderType(gender.getRequest()));
		return responseWrapper;
	}

	/**
	 * Delete a Gender Type
	 * 
	 * @param code
	 *            the code whose gender is to be deleted
	 * @return code of deleted rows
	 */
	@ResponseFilter
	@ApiOperation(value = "Delete Gender Type")
	@DeleteMapping("/gendertypes/{code}")
	public ResponseWrapper<CodeResponseDto> deleteGenderType(
			@ApiParam("Gender type Code of gender to be deleted") @PathVariable("code") String code) {
		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.deleteGenderType(code));
		return responseWrapper;
	}

	/**
	 * Validate Gender name
	 * 
	 * @param genderName
	 *            gender Name
	 * @return {@link StatusResponseDto } StatusResponseDto
	 */
	@ResponseFilter
	@ApiOperation(value = "validate gender name")
	@GetMapping("/gendertypes/validate/{gendername}")
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
	public ResponseWrapper<StatusResponseDto> valdiateGenderName(@PathVariable("gendername") String genderName) {
		ResponseWrapper<StatusResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.validateGender(genderName));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all gender types.
	 * 
	 * @param page
	 *            the page number
	 * @param size
	 *            the size of each page
	 * @param sort
	 *            the attributes by which it should be ordered
	 * @param order
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the data
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/gendertypes/all")
	@ApiOperation(value = "Retrieve all the genders with additional metadata", notes = "Retrieve all the genders with the additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of gender types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving gender types") })
	public ResponseWrapper<PageDto<GenderExtnDto>> getAllGenders(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<GenderExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(genderTypeService.getGenderTypes(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}
}
