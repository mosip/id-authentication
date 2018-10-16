package io.mosip.authentication.service.impl.indauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.authentication.service.impl.indauth.validator.DemoValidator;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests
 * 
 * @author Arun Bose
 */

@RestController
public class AuthController {

	private MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private AuthRequestValidator authRequestValidator;
	@Autowired
	DemoValidator demoValidator;

	@Autowired
	private AuthFacade authFacade;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(authRequestValidator, demoValidator);
	}

	/**
	 * authenticateRequest - method to authenticate request
	 * 
	 * 
	 * @param authrequestdto - Authenticate Request
	 * @param errors
	 * @return AuthResponseDTO
	 * @throws IdAuthenticationAppException
	 * @throws ValidateOTPException
	 */

	@PostMapping(path = "/authRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public AuthResponseDTO authenticateApplication(@Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors) throws IdAuthenticationAppException {
		AuthResponseDTO authResponsedto = null;

		try {
			DataValidationUtil.validate(errors);

			authResponsedto = authFacade.authenticateApplicant(authrequestdto);
		} catch (IDDataValidationException e) {
			logger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" :e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e);
		}
		
		return authResponsedto;
	}
}
