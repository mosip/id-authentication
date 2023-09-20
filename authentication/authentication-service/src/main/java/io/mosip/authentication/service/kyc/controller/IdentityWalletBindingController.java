package io.mosip.authentication.service.kyc.controller;

import java.util.Map;
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
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingResponseDto;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.IdentityKeyBindingFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.validator.IdentityKeyBindingRequestValidator;
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
 * The {@code IdentityWalletBindingController} used to handle to perform authentication 
 * and bind wallet key with the identity.
 *
 * @author Mahammed Taheer
 */
@RestController
@Tag(name = "identity-wallet-binding-controller", description = "Identity Wallet Binding Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
public class IdentityWalletBindingController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdentityWalletBindingController.class);

	/** The auth facade. */
	@Autowired
	private IdentityKeyBindingFacade keyIdentityFacade;
	
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;

	/** The KycExchangeRequestValidator */
	@Autowired
	private IdentityKeyBindingRequestValidator identityKeyBindingRequestValidator;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("identityKeyBindingRequestDTO")
	private void initKeyBindingAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(identityKeyBindingRequestValidator);
	} 

	/**
	 * Controller Method to auhtentication and bind key for the identity.
	 *
	 * @param identityKeyBindingRequestDTO the identity key binding request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/identity-key-binding/delegated/{IdP-LK}/{Auth-Partner-ID}/{OIDC-Client-Id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Identity Key Binding Request", description = "to authenticate and bind key with the identity", tags = { "identity-wallet-binding-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public IdentityKeyBindingResponseDto processIdKeyBinding(@Validated @RequestBody IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO,
															 @ApiIgnore Errors errors, @PathVariable("IdP-LK") String mispLK, 
															 @PathVariable("Auth-Partner-ID") String partnerId,
															 @PathVariable("OIDC-Client-Id") String oidcClientId, 
															 HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWrapperWithMetadata = (ObjectWithMetadata) request;

			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, identityKeyBindingRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(identityKeyBindingRequestDTO, false, partner);
 			try {
				
				String idType = Objects.nonNull(identityKeyBindingRequestDTO.getIndividualIdType()) ? identityKeyBindingRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(identityKeyBindingRequestDTO.getIndividualId()).getType();
				identityKeyBindingRequestDTO.setIndividualIdType(idType);
				identityKeyBindingRequestValidator.validateIdvId(identityKeyBindingRequestDTO.getIndividualId(), idType, errors);
				DataValidationUtil.validate(errors);
				
				AuthResponseDTO authResponseDTO = keyIdentityFacade.authenticateIndividual(identityKeyBindingRequestDTO, partnerId, 
										oidcClientId, requestWrapperWithMetadata);

				IdentityKeyBindingResponseDto keyBindingResponseDto = new IdentityKeyBindingResponseDto();
				Map<String, Object> metadata = requestWrapperWithMetadata.getMetadata();
				if (authResponseDTO != null && 
						metadata != null && 
								metadata.get(IdAuthCommonConstants.IDENTITY_DATA) != null &&
										metadata.get(IdAuthCommonConstants.IDENTITY_INFO) != null) {
 					keyBindingResponseDto = keyIdentityFacade.processIdentityKeyBinding(identityKeyBindingRequestDTO, authResponseDTO, 
								partnerId, oidcClientId, metadata);
				}
				return keyBindingResponseDto;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processIdKeyBinding",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.KEY_BINDIN_REQUEST_RESPONSE, identityKeyBindingRequestDTO, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				if(identityKeyBindingRequestDTO.getTransactionID() == null) 
					identityKeyBindingRequestDTO.setTransactionID(IdAuthCommonConstants.NO_TRANSACTION_ID);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, identityKeyBindingRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWrapperWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processIdKeyBinding",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.KEY_BINDIN_REQUEST_RESPONSE, identityKeyBindingRequestDTO, e);
				authTransactionHelper.setAuthTransactionEntityMetadata(e, authTxnBuilder, requestWrapperWithMetadata);
				authTransactionHelper.setAuthTransactionEntityMetadata(requestWrapperWithMetadata, authTxnBuilder);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, identityKeyBindingRequestDTO.getTransactionID());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}  
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}
}
