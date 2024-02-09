package io.mosip.authentication.service.kyc.controller;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import io.mosip.authentication.core.indauth.dto.*;
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
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.KycFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.validator.KycAuthRequestValidator;
import io.mosip.authentication.service.kyc.validator.KycExchangeRequestValidator;
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
@Tag(name = "kyc-auth-controller", description = "Kyc Auth Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
public class KycAuthController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(KycAuthController.class);

	/** The KycAuthRequestValidator */
	@Autowired
	private KycAuthRequestValidator kycReqValidator;

	/** The auth request validator. */
	@Autowired
	private AuthRequestValidator authRequestValidator;

	/** The auth facade. */
	@Autowired
	private KycFacade kycFacade;
	
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
	private KycExchangeRequestValidator kycExchangeValidator;
	
	@Autowired
	private OndemandTemplateEventPublisher ondemandTemplateEventPublisher;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("kycAuthRequestDTO")
	private void initKycAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(authRequestValidator);
	} 

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("ekycAuthRequestDTO")
	private void initEKycBinder(WebDataBinder binder) {
		binder.setValidator(kycReqValidator);
	}

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("kycExchangeRequestDTO")
	private void initKycExchangeBinder(WebDataBinder binder) {
		binder.setValidator(kycExchangeValidator);
	}

	
	/**
	 * Controller Method to auhtentication for eKyc-Details.
	 *
	 * @param ekycAuthRequestDTO the kyc auth request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/kyc/{MISP-LK}/{eKYC-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "eKyc Request", description = "eKyc Request", tags = { "kyc-auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public EKycAuthResponseDTO processKyc(@Validated @RequestBody EkycAuthRequestDTO ekycAuthRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK,@PathVariable("eKYC-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey, HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWrapperWithMetadata = (ObjectWithMetadata) request;

			boolean isAuth = true;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, ekycAuthRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(ekycAuthRequestDTO, !isAuth, partner);
			
			try {
				String idType = Objects.nonNull(ekycAuthRequestDTO.getIndividualIdType()) ? ekycAuthRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(ekycAuthRequestDTO.getIndividualId()).getType();
						ekycAuthRequestDTO.setIndividualIdType(idType);
				kycReqValidator.validateIdvId(ekycAuthRequestDTO.getIndividualId(), idType, errors);
				if(AuthTypeUtil.isBio(ekycAuthRequestDTO)) {
					kycReqValidator.validateDeviceDetails(ekycAuthRequestDTO, errors);
				}
				DataValidationUtil.validate(errors);
				boolean externalAuthRequest = true;
				AuthResponseDTO authResponseDTO = kycFacade.authenticateIndividual(ekycAuthRequestDTO, externalAuthRequest, 
														partnerId, partnerApiKey, requestWrapperWithMetadata);
				EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
				Map<String, Object> metadata = requestWrapperWithMetadata.getMetadata();
				if (authResponseDTO != null && 
						metadata != null && 
								metadata.get(IdAuthCommonConstants.IDENTITY_DATA) != null &&
										metadata.get(IdAuthCommonConstants.IDENTITY_INFO) != null) {
					kycAuthResponseDTO = kycFacade.processEKycAuth(ekycAuthRequestDTO, authResponseDTO, partnerId, metadata);
				}
				return kycAuthResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processeEKyc",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, ekycAuthRequestDTO, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, ekycAuthRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWrapperWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processEKyc",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				if (IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode().equals(e.getErrorCode())) {
					ondemandTemplateEventPublisher.notify(ekycAuthRequestDTO, request.getHeader("signature"), partner,
							e, ekycAuthRequestDTO.getMetadata());
				}
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, ekycAuthRequestDTO, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, ekycAuthRequestDTO.getTransactionID());
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWrapperWithMetadata);
			}
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

	/**
	 * Controller Method to Initiate IdP Auth (kyc-auth).
	 *
	 * @param authRequestDTO the kyc auth request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/kyc-auth/delegated/{IdP-LK}/{Auth-Partner-ID}/{OIDC-Client-Id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Kyc Auth Request", description = "Kyc Auth Request", tags = { "kyc-auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public KycAuthResponseDTO processKycAuth(@Validated @RequestBody KycAuthRequestDTO authRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("IdP-LK") String mispLK, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("OIDC-Client-Id") String oidcClientId, HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWrapperWithMetadata = (ObjectWithMetadata) request;

			boolean isAuth = true;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, authRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(authRequestDTO, !isAuth, partner);
			
			try {
				String idType = Objects.nonNull(authRequestDTO.getIndividualIdType()) ? authRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(authRequestDTO.getIndividualId()).getType();
						authRequestDTO.setIndividualIdType(idType);
				authRequestValidator.validateIdvId(authRequestDTO.getIndividualId(), idType, errors);
				if(AuthTypeUtil.isBio(authRequestDTO)) {
					kycReqValidator.validateDeviceDetails(authRequestDTO, errors);
				}
				DataValidationUtil.validate(errors);
				boolean externalAuthRequest = true;
				AuthResponseDTO authResponseDTO = kycFacade.authenticateIndividual(authRequestDTO, externalAuthRequest, partnerId, 
								oidcClientId, requestWrapperWithMetadata, IdAuthCommonConstants.KYC_AUTH_CONSUME_VID_DEFAULT);
				KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
				Map<String, Object> metadata = requestWrapperWithMetadata.getMetadata();
				if (authResponseDTO != null && 
						metadata != null && 
								metadata.get(IdAuthCommonConstants.IDENTITY_DATA) != null &&
										metadata.get(IdAuthCommonConstants.IDENTITY_INFO) != null) {
					kycAuthResponseDTO = kycFacade.processKycAuth(authRequestDTO, authResponseDTO, partnerId, oidcClientId, metadata);
				}
				return kycAuthResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycAuth",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.KYC_REQUEST_RESPONSE, authRequestDTO, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWrapperWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycAuth",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				if (IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode().equals(e.getErrorCode())) {
					ondemandTemplateEventPublisher.notify(authRequestDTO, request.getHeader("signature"), partner, e,
							authRequestDTO.getMetadata());
				}
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.KYC_REQUEST_RESPONSE, authRequestDTO, e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, authRequestDTO.getTransactionID());
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWrapperWithMetadata);
			}
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

	/**
	 * Controller Method for Kyc-exchange.
	 *
	 * @param kycExchangeRequestDTO the kyc exchange request DTO
	 * @param errors            the errors
	 * @return KycExchangeResponseDTO the kyc exchange response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/kyc-exchange/delegated/{IdP-LK}/{Auth-Partner-ID}/{OIDC-Client-Id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Kyc Exchange Request", description = "Kyc Exchange Request", tags = { "kyc-auth-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public KycExchangeResponseDTO processKycExchange(@Validated @RequestBody KycExchangeRequestDTO kycExchangeRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("IdP-LK") String mispLK, @PathVariable("Auth-Partner-ID") String partnerId,
			@PathVariable("OIDC-Client-Id") String oidcClientId, HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, kycExchangeRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(kycExchangeRequestDTO, false, partner);
 			try {
				
				String idType = Objects.nonNull(kycExchangeRequestDTO.getIndividualIdType()) ? kycExchangeRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(kycExchangeRequestDTO.getIndividualId()).getType();
				kycExchangeRequestDTO.setIndividualIdType(idType);
				kycExchangeValidator.validateIdvId(kycExchangeRequestDTO.getIndividualId(), idType, errors);
				DataValidationUtil.validate(errors);
				
				Map<String, Object> metadata = kycExchangeRequestDTO.getMetadata();
				KycExchangeResponseDTO kycExchangeResponseDTO = kycFacade.processKycExchange(kycExchangeRequestDTO, partnerId, 
									oidcClientId, metadata, requestWithMetadata);
				
				return kycExchangeResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchange",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				if(kycExchangeRequestDTO.getTransactionID() == null) 
					kycExchangeRequestDTO.setTransactionID(IdAuthCommonConstants.NO_TRANSACTION_ID);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, kycExchangeRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKycExchange",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				authTransactionHelper.setAuthTransactionEntityMetadata(e, authTxnBuilder, requestWithMetadata);
				authTransactionHelper.setAuthTransactionEntityMetadata(requestWithMetadata, authTxnBuilder);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, kycExchangeRequestDTO.getTransactionID());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}  
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}
}