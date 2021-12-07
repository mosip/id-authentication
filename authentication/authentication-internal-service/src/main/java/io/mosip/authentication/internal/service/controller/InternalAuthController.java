package io.mosip.authentication.internal.service.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the Internal authentication
 * requests.
 *
 * @author Prem Kumar
 */
@RestController
@Tag(name = "internal-auth-controller", description = "Internal Auth Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
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
	//@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT')")
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostauth())")
	@PostMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Authenticate Internal Request", description = "Authenticate Internal Request", tags = { "internal-auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public AuthResponseDTO authenticate(@Validated @RequestBody AuthRequestDTO authRequestDTO, @ApiIgnore Errors errors, HttpServletRequest request)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;

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
						DEFAULT_PARTNER_API_KEY, IdAuthCommonConstants.CONSUME_VID_DEFAULT,
						requestWithMetadata);
				return authResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
	
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
						e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
	
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
						e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authRequestDTO.getTransactionID());
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWithMetadata);
			}
		
		} else {
				mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}

	}
	
	/**
	 * Authenticate tsp.
	 * 
	 * @since 1.2.0
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors              the e
	 * @return authResponseDTO the auth response DTO
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 * 
	 * 
	 */
	//@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR','REGISTRATION_ADMIN','REGISTRATION_OFFICER','REGISTRATION_SUPERVISOR','RESIDENT')")
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostverifyidentity())")
	@PostMapping(path = "/verifyidentity", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Authenticate Internal Request", description = "Authenticate Internal Request", tags = { "internal-auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public AuthResponseDTO authenticateInternal(@Validated @RequestBody AuthRequestDTO authRequestDTO, @ApiIgnore Errors errors, HttpServletRequest request)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
			
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
						DEFAULT_PARTNER_API_KEY, IdAuthCommonConstants.CONSUME_VID_DEFAULT,
						requestWithMetadata);
				return authResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
	
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
						e);
	
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "authenticateApplicant",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
	
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.INTERNAL_REQUEST_RESPONSE, authRequestDTO,
						e);
	
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWithMetadata);
			}
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}

	}

}
