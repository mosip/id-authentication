package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provide services to MOSIP system to do CRUD operations on
 * Languages.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RestController
@RequestMapping("/languages")
@Api(tags = { "languages" })
public class LanguageController {

	/**
	 * Service provide CRUD operation over Languages.
	 */
	@Autowired
	private LanguageService languageService;

	@GetMapping
	@ApiOperation(value = "Retrieve all Languages", notes = "Retrieve all Languages", response = LanguageResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When all Language retrieved from database", response = LanguageResponseDto.class),
			@ApiResponse(code = 404, message = "When No Language found"),
			@ApiResponse(code = 500, message = "While retrieving Language any error occured") })
	public LanguageResponseDto getAllLaguages() {
		return languageService.getAllLaguages();
	}

	@PostMapping
	@ApiOperation(value = "Service to save Language", notes = "Saves Language and return Language code", response = CodeResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 201, message = "When Language successfully created", response = CodeResponseDto.class),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 500, message = "While creating Language any error occured") })
	public ResponseEntity<CodeResponseDto> saveLanguage(@Valid @RequestBody RequestDto<LanguageDto> language) {
		return new ResponseEntity<>(languageService.saveLanguage(language), HttpStatus.CREATED);
	}

}
