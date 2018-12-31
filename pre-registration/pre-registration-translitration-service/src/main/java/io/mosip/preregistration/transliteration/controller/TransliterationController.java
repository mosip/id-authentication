/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.preregistration.transliteration.dto.TransliterationDTO;
import io.mosip.preregistration.transliteration.service.TransliterationService;
import io.mosip.preregistration.transliteration.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.dto.MainRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * This class provides different API's to perform operations on
 * Transliteration Application
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class TransliterationController {

	/** Autowired reference for {@link #transliterationService}. */
	@Autowired
	private TransliterationService transliterationService;

	/**
	 * Post API to transliterate from transliteration application.
	 * 
	 * @param requestDTO
	 * @return responseDto with transliterated toFieldValue. 
	 */
	@PostMapping(path = "/translitrate", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion-Translitration data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Given key is translitrated successfully"),
			@ApiResponse(code = 400, message = "Unable to get the translitration") })
	public ResponseEntity<MainResponseDTO<TransliterationDTO>> translitrator(
			@RequestBody(required = true) MainRequestDTO<TransliterationDTO> requestDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(transliterationService.translitratorService(requestDTO));
	}
}
