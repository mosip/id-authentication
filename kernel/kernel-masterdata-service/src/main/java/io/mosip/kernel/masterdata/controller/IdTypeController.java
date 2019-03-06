package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.IdTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This controller class provides id types master data operations.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "IdType" })
public class IdTypeController {
	/**
	 * Autowired reference to IdService.
	 */
	@Autowired
	IdTypeService idService;

	/**
	 * This method returns the list of id types present for a specific language
	 * code.
	 * 
	 * @param langCode
	 *            the language code against which id types are to be fetched.
	 * @return the list of id types.
	 */
	@GetMapping("/v1.0/idtypes/{langcode}")
	@ApiOperation(value = "Service to fetch id types based on language code.", notes = "Fetch IdTypes based on Language Code.", response = IdTypeResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When idtypes successfully fetched.", response = IdTypeResponseDto.class),
			@ApiResponse(code = 400, message = "When input request has null or invalid values."),
			@ApiResponse(code = 404, message = "When no idtypes found."),
			@ApiResponse(code = 500, message = "Error occured while fetching id types.") })
	public IdTypeResponseDto getIdTypesByLanguageCode(@Valid @PathVariable("langcode") String langCode) {
		return idService.getIdTypesByLanguageCode(langCode);
	}

	/**
	 * This method creates id types.
	 * 
	 * @param idTypeRequestDto
	 *            the request of idtype to be added.
	 * @return the response.
	 */
	@PostMapping("/v1.0/idtypes")
	@ApiOperation(value = "Service to create id type.", notes = "Create Id Type.", response = CodeAndLanguageCodeID.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When id type successfully created.", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When input request has null or invalid values."),
			@ApiResponse(code = 500, message = "Error occured while creating id type.") })
	public ResponseEntity<CodeAndLanguageCodeID> createIdType(
			@Valid @RequestBody RequestDto<IdTypeDto> idTypeRequestDto) {
		return new ResponseEntity<>(idService.createIdType(idTypeRequestDto), HttpStatus.OK);
	}
}
