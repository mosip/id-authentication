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
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.indauth.facade.VciFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.kyc.validator.VciExchangeRequestValidator;
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
 * The {@code VCIController} used to validate the issued authentication
 * token and issue verifiable credentials after successful validation.
 *
 * @author Mahammed Taheer
 */
@RestController
@Tag(name = "vci-controller", description = "Verifiable Credential Issuance Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
public class VCIController {

	/** The mosipLogger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdentityWalletBindingController.class);

	/** The vci facade. */
	@Autowired
	private VciFacade vciFacade;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;

	/** The KycExchangeRequestValidator */
	@Autowired
	private VciExchangeRequestValidator vciExchangeRequestValidator;

	/**
	 *
	 * @param binder the binder
	 */
	@InitBinder("vciExchangeRequestDTO")
	private void initKeyBindingAuthRequestBinder(WebDataBinder binder) {
		binder.setValidator(vciExchangeRequestValidator);
	} 

	/**
	 * Controller Method to validate the token returned after successful authentication and 
	 * returns a Verifiable Credential.
	 *
	 * @param vciExchangeRequestDTO the VCI Exchange request DTO
	 * @param errors            the errors
	 * @return kycAuthResponseDTO the kyc response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationDaoException      the id authentication dao exception
	 */
	@PostMapping(path = "/vci-exchange/delegated/{IdP-LK}/{Auth-Partner-ID}/{OIDC-Client-Id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Verifiable Credential Issuance Request", description = "to issue verifiable credential after token validation", tags = { "vci-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
	@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
	@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
	@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
	@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public VciExchangeResponseDTO vciExchange(@Validated @RequestBody VciExchangeRequestDTO vciExchangeRequestDTO,
													 @ApiIgnore Errors errors, @PathVariable("IdP-LK") String idpLK, 
													 @PathVariable("Auth-Partner-ID") String partnerId,
													 @PathVariable("OIDC-Client-Id") String oidcClientId, 
													 HttpServletRequest request)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException {
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWrapperWithMetadata = (ObjectWithMetadata) request;

			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, vciExchangeRequestDTO.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(vciExchangeRequestDTO, false, partner);
 			try {
				
				String idType = Objects.nonNull(vciExchangeRequestDTO.getIndividualIdType()) ? vciExchangeRequestDTO.getIndividualIdType()
						: idTypeUtil.getIdType(vciExchangeRequestDTO.getIndividualId()).getType();
				vciExchangeRequestDTO.setIndividualIdType(idType);
				vciExchangeRequestValidator.validateIdvId(vciExchangeRequestDTO.getIndividualId(), idType, errors);
				DataValidationUtil.validate(errors);
				
				Map<String, Object> metadata = vciExchangeRequestDTO.getMetadata();
				VciExchangeResponseDTO vciExchangeResponseDTO = vciFacade.processVciExchange(vciExchangeRequestDTO, partnerId, 
									oidcClientId, metadata, requestWrapperWithMetadata);
				
				return vciExchangeResponseDTO;
			} catch (IDDataValidationException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "vciExchange",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				if(vciExchangeRequestDTO.getTransactionID() == null) 
					vciExchangeRequestDTO.setTransactionID(IdAuthCommonConstants.NO_TRANSACTION_ID);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, vciExchangeRequestDTO.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWrapperWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "vciExchange",
						e.getErrorTexts().isEmpty() ? "" : e.getErrorText());
				
				authTransactionHelper.setAuthTransactionEntityMetadata(e, authTxnBuilder, requestWrapperWithMetadata);
				authTransactionHelper.setAuthTransactionEntityMetadata(requestWrapperWithMetadata, authTxnBuilder);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWrapperWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, vciExchangeRequestDTO.getTransactionID());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}  
		} else {
			mosipLogger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		} 
	}
}