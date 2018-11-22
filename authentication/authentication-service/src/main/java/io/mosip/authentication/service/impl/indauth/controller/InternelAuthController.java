package io.mosip.authentication.service.impl.indauth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.service.impl.indauth.validator.InternalAuthRequestValidator;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Prem Kumar
 */
@RestController
public class InternelAuthController {

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;
	
	/** The internal Auth Request Validator*/
	@Autowired
	private InternalAuthRequestValidator internalAuthRequestValidator;

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(internalAuthRequestValidator);
	}

	/**
	 * @throws IdAuthenticationAppException 
	 * 
	 * 
	 * 
	 */
	@PostMapping(path="/auth/internal",consumes=MediaType.APPLICATION_JSON_UTF8_VALUE,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public AuthResponseDTO authenticateTsp(@RequestBody AuthRequestDTO authRequestDTO,@ApiIgnore Errors e) throws IdAuthenticationAppException
	{
		AuthResponseDTO authResponseDTO=null;
		try {
			DataValidationUtil.validate(e);
			 authResponseDTO=authFacade.authenticateTsp(authRequestDTO);
		} catch (IDDataValidationException e1) {
			
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e1);
		
		}
		
		return authResponseDTO;
	}
}
