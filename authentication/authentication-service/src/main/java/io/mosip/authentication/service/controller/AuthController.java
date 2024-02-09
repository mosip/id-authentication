package io.mosip.authentication.service.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.websub.impl.OndemandTemplateEventPublisher;
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
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code AuthController} used to handle all the authentication requests.
 *
 * @author Arun Bose
 * @author Prem Kumar
 * @author Nagarjuna K
 */
@RestController
@Tag(name = "auth-controller", description = "Auth Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
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
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private OndemandTemplateEventPublisher ondemandTemplateEventPublisher;


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
	@PostMapping(path = "/auth/{MISP-LK}/{Auth-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Authenticate Request", description = "Authenticate Request", tags = { "auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public AuthResponseDTO authenticateIndividual(@Validated @RequestBody AuthRequestDTO authrequestdto,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey, HttpServletRequest request)
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {
		
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
		
			boolean isAuth = true;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, authrequestdto.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(authrequestdto, !isAuth, partner);
			
			try {
				String idType = Objects.nonNull(authrequestdto.getIndividualIdType()) ? authrequestdto.getIndividualIdType()
						: idTypeUtil.getIdType(authrequestdto.getIndividualId()).getType();
				authrequestdto.setIndividualIdType(idType);
				authRequestValidator.validateIdvId(authrequestdto.getIndividualId(), idType, errors);
				if(!errors.hasErrors() && AuthTypeUtil.isBio(authrequestdto)) {
					authRequestValidator.validateDeviceDetails(authrequestdto, errors);
				}
				DataValidationUtil.validate(errors);
				AuthResponseDTO authResponsedto = authFacade.authenticateIndividual(authrequestdto, true, partnerId,
						partnerApiKey, IdAuthCommonConstants.CONSUME_VID_DEFAULT, requestWithMetadata);
				// Note: Auditing of success or failure status of each authentication (but not
				// the exception) is handled in respective authentication invocations in the facade
				return authResponsedto;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						"authenticateApplication", e.getErrorCode() + " : " + e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authrequestdto.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						"authenticateApplication", e.getErrorCode() + " : " + e.getErrorText());
				if (IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode().equals(e.getErrorCode())) {
					ondemandTemplateEventPublisher.notify(authrequestdto, request.getHeader("signature"), partner, e,
							authrequestdto.getMetadata());
				}
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.AUTH_REQUEST_RESPONSE, authrequestdto, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authrequestdto.getTransactionID());
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWithMetadata);
			} 
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

	
}