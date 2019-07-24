package io.mosip.registration.processor.request.handler.service.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.request.handler.service.dto.UinCardRePrintRequestDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.upload.validator.RequestHandlerRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Ranjitha
 *
 */
@RestController
@Api(tags = "Uin Card RePrint")
public class UinCardRePrintController {
	
	/** Token validator class */
	@Autowired
	TokenValidator tokenValidator;
	
	/** The validator. */
	@Autowired
	private RequestHandlerRequestValidator validator;
	
	@ApiOperation(value = "Uin Card Re-Print Api", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Uin Card Re-Print Api"),
			@ApiResponse(code = 400, message = "Unable to fetch the status "),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PostMapping(path = "/reprint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getStatus(
			@RequestBody(required = true) UinCardRePrintRequestDto uinCardRePrintRequestDto,
			@CookieValue(value = "Authorization", required = true) String token)
					throws RegBaseCheckedException, IOException {
		tokenValidator.validate("Authorization=" + token, "requesthandler");
		try {	
			validator.validate(uinCardRePrintRequestDto.getRequesttime(),
					uinCardRePrintRequestDto.getId(), uinCardRePrintRequestDto.getVersion());
			
		}catch(Exception e) {
			
		}
		return null;
	}
}
