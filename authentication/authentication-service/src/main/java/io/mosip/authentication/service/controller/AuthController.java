package io.mosip.authentication.service.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Arun Bose
 * @author Prem Kumar
 * @author Nagarjuna K
 */
@RestController
public class AuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(AuthController.class);

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;
	
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;


	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("authRequestDTO")
	private void initAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(authRequestValidator);
	}

	/**
	 * authenticateRequest - method to authenticate request.
	 *
	 * @param authrequestdto - Authenticate Request
	 * @param errors         the errors
	 * @return authResponsedto AuthResponseDTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 * @throws IdAuthenticationBusinessException
	 */
	@PostMapping(path = "/{MISP-LK}/{Auth-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Authenticate Request", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public AuthResponseDTO authenticateIndividual(@Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey)
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		AuthResponseDTO authResponsedto = null;
		try {
			String idType = Objects.nonNull(authrequestdto.getIndividualIdType()) ? authrequestdto.getIndividualIdType()
					: idTypeUtil.getIdType(authrequestdto.getIndividualId()).name();
			authRequestValidator.validateIdvId(authrequestdto.getIndividualId(), idType, errors);
			if(Optional.of(authrequestdto)
					.map(AuthRequestDTO::getRequestedAuth)
					.filter(AuthTypeDTO::isBio)
					.isPresent()) {
				authRequestValidator.validateDeviceDetails(authrequestdto, errors);
			}
			DataValidationUtil.validate(errors);
			authResponsedto = authFacade.authenticateIndividual(authrequestdto, true, partnerId, partnerApiKey);
			// Note: Auditing of success or failure status of each authentication (but not
			// the exception) is handled in respective authentication invocations in the facade
		} catch (IDDataValidationException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication", e.getErrorCode() + " : " + e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
			
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					"authenticateApplication",  e.getErrorCode() + " : " + e.getErrorText());
			
			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
			
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} 
		return authResponsedto;
	}

	
}
