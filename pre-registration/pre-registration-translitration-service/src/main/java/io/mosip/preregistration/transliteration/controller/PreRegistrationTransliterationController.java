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

import io.mosip.preregistration.transliteration.dto.CreateTransliterationRequest;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationRequestDTO;
import io.mosip.preregistration.transliteration.service.impl.TransliterationServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/pre-registration/")
@Api(tags = "Pre-Registration")
@CrossOrigin("*")
public class PreRegistrationTransliterationController {

	@Autowired
	private TransliterationServiceImpl transliterationServiceImpl;

	@PostMapping(path = "/translitrate", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Pre-Registartion-Translitration data")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Given key is translitrated successfully"),
			@ApiResponse(code = 400, message = "Unable to get the translitration") })
	public ResponseEntity<ResponseDTO<CreateTransliterationRequest>> translitrator(
			@RequestBody(required = true) TransliterationRequestDTO<CreateTransliterationRequest> reuestDTO) {
		return ResponseEntity.status(HttpStatus.OK).body(transliterationServiceImpl.translitratorService(reuestDTO));

	}

}
