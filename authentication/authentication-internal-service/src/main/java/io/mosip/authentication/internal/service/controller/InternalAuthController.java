package io.mosip.authentication.internal.service.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.PublicKeyResponseDto;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.keymanager.service.KeymanagerService;
import io.mosip.authentication.internal.service.validator.InternalAuthRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the Internal authentication
 * requests.
 *
 * @author Prem Kumar
 */
@RestController
public class InternalAuthController {

	@Autowired
	private Environment env;
	
	/**
	 * Instance for KeymanagerService
	 */
	@Autowired
	KeymanagerService keymanagerService;
	
	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;

	/** The internal Auth Request Validator */
	@Autowired
	private InternalAuthRequestValidator internalAuthRequestValidator;
	
	@Autowired
	private AuditHelper auditHelper;

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(InternalAuthController.class);

	public static final String DEFAULT_PARTNER_ID = "INTERNAL";

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(internalAuthRequestValidator);
	}

	/**
	 * Authenticate tsp.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param e              the e
	 * @return authResponseDTO the auth response DTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT')")
	@PostMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Internal Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public AuthResponseDTO authenticate(@Validated @RequestBody AuthRequestDTO authRequestDTO, @ApiIgnore Errors e)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthResponseDTO authResponseDTO = null;
		try {
			DataValidationUtil.validate(e);
			authResponseDTO = authFacade.authenticateIndividual(authRequestDTO, false, DEFAULT_PARTNER_ID);
		} catch (IDDataValidationException e1) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e1.getErrorTexts().isEmpty() ? "" : e1.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO, e1);
			
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e1);
		} catch (IdAuthenticationBusinessException e1) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e1.getErrorTexts().isEmpty() ? "" : e1.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO, e1);
			
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e1);
		}

		return authResponseDTO;
	}
	
	/**
	 * Request mapping to get Public Key
	 * 
	 * @param applicationId Application id of the application requesting publicKey
	 * @param timeStamp     Timestamp of the request
	 * @param referenceId   Reference id of the application requesting publicKey
	 * @return {@link PublicKeyResponse} instance
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT')")	
	@GetMapping(value = "/publickey/{applicationId}")
	public PublicKeyResponseDto<String> getPublicKey(
			@ApiParam("Id of application") @PathVariable("applicationId") String applicationId,
			@ApiParam("Timestamp as metadata") @RequestParam("timeStamp") String timestamp,
			@ApiParam("Refrence Id as metadata") @RequestParam("referenceId") Optional<String> referenceId) {
		if (applicationId.equalsIgnoreCase(env.getProperty(IdAuthConfigKeyConstants.IDA_SIGN_APPID)) && referenceId.isPresent()
				&& referenceId.get().equals(env.getProperty(IdAuthConfigKeyConstants.IDA_SIGN_REFID))) {
			return keymanagerService.getSignPublicKey(applicationId, timestamp, referenceId);
		} else {
			return keymanagerService.getPublicKey(applicationId, timestamp, referenceId);
		}
	}

}
