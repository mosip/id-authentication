package io.mosip.authentication.service.kyc.controller;

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
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.facade.KycFacadeImpl;
import io.mosip.authentication.service.kyc.validator.KycAuthRequestValidator;
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

	/** The auth facade. */
	@Autowired
	private KycFacadeImpl kycFacade;
	
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("kycAuthRequestDTO")
	private void initKycBinder(WebDataBinder binder) {
		binder.addValidators(kycReqValidator);
	}

	/**
	 * Controller Method to auhtentication for eKyc-Details.
	 *
	 * @param kycAuthRequestDTO the kyc auth request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc auth response DTO
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
	public KycAuthResponseDTO processKyc(@Validated @RequestBody KycAuthRequestDTO kycAuthRequestDTO,
			@ApiIgnore Errors errors, @PathVariable("MISP-LK") String mispLK,@PathVariable("eKYC-Partner-ID") String partnerId,
			@PathVariable("API-Key") String partnerApiKey, HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;

			boolean isAuth = true;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, kycAuthRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(kycAuthRequestDTO, !isAuth, partner);
			
			try {
				String idType = Objects.nonNull(kycAuthRequestDTO.getIndividualIdType()) ? kycAuthRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(kycAuthRequestDTO.getIndividualId()).getType();
				kycAuthRequestDTO.setIndividualIdType(idType);
				kycReqValidator.validateIdvId(kycAuthRequestDTO.getIndividualId(), idType, errors);
				if(AuthTypeUtil.isBio(kycAuthRequestDTO)) {
					kycReqValidator.validateDeviceDetails(kycAuthRequestDTO, errors);
				}
				DataValidationUtil.validate(errors);
				
				AuthResponseDTO authResponseDTO = kycFacade.authenticateIndividual(kycAuthRequestDTO, true, partnerId, partnerApiKey, requestWithMetadata);
				KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
				if (authResponseDTO != null && 
						authResponseDTO.getMetadata() != null && 
						authResponseDTO.getMetadata().get(IdAuthCommonConstants.IDENTITY_DATA) != null &&
						authResponseDTO.getMetadata().get(IdAuthCommonConstants.IDENTITY_INFO) != null) {
					kycAuthResponseDTO = kycFacade.processKycAuth(kycAuthRequestDTO, authResponseDTO, partnerId);
				}
				return kycAuthResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, kycAuthRequestDTO, e);
				
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processKyc",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				auditHelper.auditExceptionForAuthRequestedModules(AuditEvents.EKYC_REQUEST_RESPONSE, kycAuthRequestDTO, e);
				
				throw authTransactionHelper.createUnableToProcessException(authTxnBuilder, e, requestWithMetadata);
			}
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

}
