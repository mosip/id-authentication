package io.mosip.authentication.internal.service.controller;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.internal.service.validator.InternalAuthRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.ApiOperation;
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

	/** The auth facade. */
	@Autowired
	private AuthFacade authFacade;

	/** The internal Auth Request Validator */
	@Autowired
	private InternalAuthRequestValidator internalAuthRequestValidator;

	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private IdTypeUtil idTypeUtil;

	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(InternalAuthController.class);

	public static final String DEFAULT_PARTNER_API_KEY = "INTERNAL_API_KEY";

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
	 * @param errors              the e
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
	public AuthResponseDTO authenticate(@Validated @RequestBody AuthRequestDTO authRequestDTO, @ApiIgnore Errors errors)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		boolean isAuth = false;
		Optional<PartnerDTO> partner = Optional.empty();
		AuthTransactionBuilder authTxnBuilder = authTransactionHelper
				.createAndSetAuthTxnBuilderMetadataToRequest(authRequestDTO, !isAuth, partner);

		try {
			String idType = Objects.nonNull(authRequestDTO.getIndividualIdType()) ? authRequestDTO.getIndividualIdType()
					: idTypeUtil.getIdType(authRequestDTO.getIndividualId()).getType();
			authRequestDTO.setIndividualIdType(idType);
			internalAuthRequestValidator.validateIdvId(authRequestDTO.getIndividualId(), idType, errors);
			
			
			DataValidationUtil.validate(errors);
			String partnerId = securityManager.getUser().replace(IdAuthCommonConstants.SERVICE_ACCOUNT,"");
			AuthResponseDTO authResponseDTO = authFacade.authenticateIndividual(authRequestDTO, isAuth,
					partnerId,
					DEFAULT_PARTNER_API_KEY, IdAuthCommonConstants.CONSUME_VID_DEFAULT);
			return authResponseDTO;
		} catch (IDDataValidationException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());

			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
					e);

			throw authTransactionHelper.createDataValidationException(authTxnBuilder, e);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
					e.getErrorTexts().isEmpty() ? "" : e.getErrorText());

			auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
					e);

			throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e);
		}

	}

}
