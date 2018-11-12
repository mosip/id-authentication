package io.mosip.authentication.service.impl.indauth.controller;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.authentication.service.impl.indauth.validator.KycAuthRequestValidator;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Arun Bose
 * 
 * 
 * @author Prem Kumar
 */
@RestController
public class AuthController {

	/** The mosipLogger. */
	private MosipLogger mosipLogger = IdaLogger.getLogger(AuthController.class);

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	@Autowired
	private KycAuthRequestValidator kycReqValidator;

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;

	@Autowired
	private KycService kycService;

	/**
	 *
	 * @param binder
	 *            the binder
	 */
	@InitBinder("authRequest")
	private void initAuthRequestBinder(WebDataBinder binder) {
		binder.addValidators(authRequestValidator);
	}
	
	/**
	 *
	 * @param binder
	 *            the binder
	 */
	@InitBinder("kycRequest")
	private void initKycBinder(WebDataBinder binder) {
		binder.addValidators(kycReqValidator);
	}

	/**
	 * authenticateRequest - method to authenticate request.
	 *
	 * @param authrequestdto
	 *            - Authenticate Request
	 * @param errors
	 *            the errors
	 * @return AuthResponseDTO
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */

	@PostMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public AuthResponseDTO authenticateApplication(@ModelAttribute("authRequest") @Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors) throws IdAuthenticationAppException {
		AuthResponseDTO authResponsedto = null;

		try {
			DataValidationUtil.validate(errors);

			authResponsedto = authFacade.authenticateApplicant(authrequestdto);
		} catch (IDDataValidationException e) {
			mosipLogger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e);
		}

		return authResponsedto;
	}

	/**
	 * 
	 * Method to auhtentication for eKyc-Details
	 * 
	 * @throws IdAuthenticationBusinessException
	 * @throws IdAuthenticationAppException
	 * 
	 * 
	 */
	@PostMapping(path = "/ekyc", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "eKyc Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully"),
			@ApiResponse(code = 400, message = "Request authenticated failed") })
	public KycAuthResponseDTO processKyc(@ModelAttribute("kycRequest") @Validated @RequestBody KycAuthRequestDTO kycAuthRequestDTO,
			@ApiIgnore Errors errors) throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		AuthResponseDTO authResponseDTO = null;
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		try {

			DataValidationUtil.validate(errors);

			authResponseDTO = authFacade.authenticateApplicant(kycAuthRequestDTO.getAuthRequest());
			if (authResponseDTO != null) {
				kycAuthResponseDTO = authFacade.processKycAuth(kycAuthRequestDTO);
				kycAuthResponseDTO.getResponse().setAuth(authResponseDTO);
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error("sessionId", null, null, e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e);
		}

		return kycAuthResponseDTO;
	}

}
