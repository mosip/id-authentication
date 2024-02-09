package io.mosip.authentication.otp.service.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.common.service.websub.impl.OndemandTemplateEventPublisher;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code OTPAuthController} use to send request to generate otp.
 * 
 * @author Rakesh Roshan
 * @author Nagarjuna K
 */
@RestController
@Tag(name = "otp-controller", description = "OTP Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
public class OTPController {

	private static final String GENERATE_OTP = "generateOTP";

	private static Logger logger = IdaLogger.getLogger(OTPController.class);

	@Autowired
	private OTPService otpService;

	@Autowired
	private OTPRequestValidator otpRequestValidator;
	
	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	
	@Autowired
	private PartnerService partnerService;

	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private OndemandTemplateEventPublisher ondemandTemplateEventPublisher;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(otpRequestValidator);
	}

	/**
	 * send OtpRequestDTO request to generate OTP and received OtpResponseDTO as
	 * output.
	 *
	 * @param otpRequestDto as request body
	 * @param errors        associate error
	 * @param partnerId the partner id
	 * @param mispLK the misp LK
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@PostMapping(path = "/{MISP-LK}/{Auth-Partner-ID}/{API-Key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "generateOTP", description = "generateOTP"
			, tags = { "otp-controller" })
	@SecurityRequirement(name = "Authorization")
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, @ApiIgnore Errors errors,
			@PathVariable("MISP-LK") String mispLK,@PathVariable("Auth-Partner-ID") String partnerId, @PathVariable("API-Key") String apiKey, HttpServletRequest request )
			throws IdAuthenticationAppException, IDDataValidationException, IdAuthenticationBusinessException {
		
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
			
			boolean isPartnerReq = true;
			Optional<PartnerDTO> partner = partnerService.getPartner(partnerId, otpRequestDto.getMetadata());
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(otpRequestDto, !isPartnerReq, partner);
			
			try {
				String idvidHash = securityManager.hash(otpRequestDto.getIndividualId());
				String idType = Objects.nonNull(otpRequestDto.getIndividualIdType()) ? otpRequestDto.getIndividualIdType()
						: idTypeUtil.getIdType(otpRequestDto.getIndividualId()).getType();
				otpRequestDto.setIndividualIdType(idType);
				otpRequestValidator.validateIdvId(otpRequestDto.getIndividualId(), idType, errors, IdAuthCommonConstants.IDV_ID);
				DataValidationUtil.validate(errors);
				OtpResponseDTO otpResponseDTO = otpService.generateOtp(otpRequestDto, partnerId, requestWithMetadata);
				logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
						otpResponseDTO.getResponseTime());
				
				boolean status = otpResponseDTO.getErrors() == null || otpResponseDTO.getErrors().isEmpty();
				auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getTransactionID(),
						IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), "otpRequest status : " + status);
				return otpResponseDTO;
			} catch (IDDataValidationException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
						e.getErrorText());
				auditHelper.audit(AuditModules.OTP_REQUEST,  AuditEvents.OTP_TRIGGER_REQUEST_RESPONSE , otpRequestDto.getTransactionID(),
						IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, otpRequestDto.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
				if (IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode().equals(e.getErrorCode())) {
					ondemandTemplateEventPublisher.notify(otpRequestDto, request.getHeader("signature"), partner, e,
							otpRequestDto.getMetadata());
				}
				auditHelper.audit(AuditModules.OTP_REQUEST,  AuditEvents.OTP_TRIGGER_REQUEST_RESPONSE , otpRequestDto.getTransactionID(),
						IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), e);
				authTransactionHelper.setAuthTransactionEntityMetadata(requestWithMetadata, authTxnBuilder);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, otpRequestDto.getTransactionID());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}
		
		} else {
			logger.error("Technical error. HttpServletRequest is not instanceof ObjectWithMetada.");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
	}

}
