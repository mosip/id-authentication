package org.mosip.auth.service.impl.indauth.controller;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.indauth.facade.AuthFacade;
import org.mosip.auth.core.util.DataValidationUtil;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The {@code AuthController} used to handle all the authentication requests
 * 
 * @author Arun Bose
 */

@RestController
@RequestMapping(value = "/v0.1/id-usage-service")
@Api(tags = "Authentication Request")
public class AuthController {
	
	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}
	
	
	@Autowired
	private AuthFacade authFacade;

	/**
	 * authenticateRequest - method to authenticate request
	 * 
	 * 
	 * @param authrequestdto
	 *            - Authenticate Request
	 * @param errors
	 * @return AuthResponseDTO
	 * @throws IdAuthenticationAppException 
	 * @throws ValidateOTPException
	 */

	@PostMapping(path = "/authRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Aurthenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public AuthResponseDTO authenticateApplication(@Validated @RequestBody AuthRequestDTO authrequestdto,
			Errors errors) throws IdAuthenticationAppException {
		AuthResponseDTO authResponsedto = null;

		if (errors.hasErrors()) {
			try {
				DataValidationUtil.validate(errors);
			} catch (IDDataValidationException e) {
				logger.error("sessionId", null, null, e.getErrorText());
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
				
			}
		}
		else {
			try {
				authResponsedto=authFacade.authenticateApplicant(authrequestdto);
			} catch (IdAuthenticationBusinessException e) {
				logger.error("sessionId", null, null, e.getErrorText());
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e);
			}
		  }

		return authResponsedto;

	}
}
