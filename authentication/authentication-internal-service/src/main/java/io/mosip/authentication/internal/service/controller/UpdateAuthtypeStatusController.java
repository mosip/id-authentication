package io.mosip.authentication.internal.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusRequestDto;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.internal.service.validator.UpdateAuthtypeStatusValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class UpdateAuthtypeStatusController {
	
	@Autowired
	private UpdateAuthtypeStatusValidator updateAuthtypeStatusValidator;
	
	@Autowired
	private UpdateAuthtypeStatusService updateAuthtypeStatusService;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(updateAuthtypeStatusValidator);
	}

	@PostMapping(path = "/internal/authtypes/status", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiOperation(value = "Authenticate Internal Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public UpdateAuthtypeStatusResponseDto updateAuthtypeStatus(
			@Validated @RequestBody UpdateAuthtypeStatusRequestDto updateAuthtypeStatusRequestDto, @ApiIgnore Errors e) {
		
		try {
			DataValidationUtil.validate(e);
			updateAuthtypeStatusService.UpdateAuthtypeStatus(updateAuthtypeStatusRequestDto);
		} catch (IDDataValidationException e1) {
			
		}

		return null;
	}

}
