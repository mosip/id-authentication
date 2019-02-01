package io.mosip.registration.processor.abis.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import io.mosip.registration.processor.abis.dto.AbisIdentifyRequestDto;
import io.mosip.registration.processor.abis.dto.AbisIdentifyResponseDto;
import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponseDto;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class AbisController.
 */
@RefreshScope
@RestController
@RequestMapping("/v0.1/registration-processor/abis")
@Api(tags = "Abis")
public class AbisController {

	/** The abis service. */
	@Autowired
	private AbisService abisService;

	/**
	 * Insert.
	 *
	 * @param abisInsertRequestDto the abis insert request dto
	 * @return the response entity
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	@PostMapping(path = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "insert biometric data of an Individual", response = AbisInsertResponseDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Biometric data inserted successfully"),
			@ApiResponse(code = 400, message = "Uable to insert biometric data") })
	public ResponseEntity<AbisInsertResponseDto> insert(
			@RequestBody(required = true) AbisInsertRequestDto abisInsertRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {

		AbisInsertResponseDto abisInsertResponseDto = abisService.insert(abisInsertRequestDto);

		if (abisInsertRequestDto.getId().equalsIgnoreCase("insert")) {
			return ResponseEntity.status(HttpStatus.OK).body(abisInsertResponseDto);
		} else {		
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(abisInsertResponseDto);
		}
	}

	/**
	 * Identity.
	 *
	 * @param identifyRequestDto the identify request dto
	 * @return the response entity
	 * @throws ApisResourceAccessException the apis resource access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	@PostMapping(path = "/identity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "identify duplicate biometric data of an Individual", response = AbisIdentifyResponseDto.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "unique biometric data"),
			@ApiResponse(code = 400, message = "duplicate biometric data") })
	public ResponseEntity<AbisIdentifyResponseDto> identity(
			@RequestBody(required = true) AbisIdentifyRequestDto identifyRequestDto)
			throws ApisResourceAccessException, IOException, ParserConfigurationException, SAXException {

		AbisIdentifyResponseDto identifyResponseDto = abisService.performDedupe(identifyRequestDto);

		if (identifyRequestDto.getId().equalsIgnoreCase("identify")) {
			return ResponseEntity.status(HttpStatus.OK).body(identifyResponseDto);
		} else {
			identifyResponseDto.setCandidateList(null);
			identifyResponseDto.setReturnValue(2);
			identifyResponseDto.setFailureReason(1);
			return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(identifyResponseDto);
		}
	}
}
