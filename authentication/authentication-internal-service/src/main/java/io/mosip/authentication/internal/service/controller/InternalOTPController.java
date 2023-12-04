package io.mosip.authentication.internal.service.controller;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
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
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.internal.service.validator.InternalOTPRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code InternalOTPController} use to send request to generate otp.
 * 
 * @author Dinesh Karuppiah.T
 */
@RestController
@Tag(name = "internal-otp-controller", description = "Internal OTP Controller")
@SecurityScheme(in = SecuritySchemeIn.HEADER, scheme = "basic", type = SecuritySchemeType.APIKEY, name = "Authorization")
public class InternalOTPController {

	private static final String GENERATE_OTP = "generateOTP";

	private static Logger logger = IdaLogger.getLogger(InternalOTPController.class);

	@Autowired
	private OTPService otpService;

	@Autowired
	private InternalOTPRequestValidator otpRequestValidator;
	
	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	@Autowired
	private AuthTransactionHelper authTransactionHelper;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(otpRequestValidator);
	}

	/**
	 * send OtpRequestDTO request to generate OTP and received OtpResponseDTO as
	 * output.
	 *
	 * @param otpRequestDto as request body
	 * @param errors        associate error
	 * @param partnerId     the partner id
	 * @param mispLK        the misp LK
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IdAuthenticationBusinessException 
	 */
	//@PreAuthorize("hasAnyRole('RESIDENT')")
	@PreAuthorize("hasAnyRole(@authorizedRoles.getPostotp())")
	@PostMapping(path = "/otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "generateOTP", description = "generateOTP", tags = { "internal-otp-controller" })
	@SecurityRequirement(name = "Authorization")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, @ApiIgnore Errors errors, HttpServletRequest request)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		
		if(request instanceof ObjectWithMetadata) {
			ObjectWithMetadata requestWithMetadata = (ObjectWithMetadata) request;
			
			boolean isPartnerReq = false;
			Optional<PartnerDTO> partner = Optional.empty();
			AuthTransactionBuilder authTxnBuilder = authTransactionHelper
					.createAndSetAuthTxnBuilderMetadataToRequest(otpRequestDto, !isPartnerReq, partner);
			
			try {
				String idType = Objects.nonNull(otpRequestDto.getIndividualIdType()) ? otpRequestDto.getIndividualIdType()
						: idTypeUtil.getIdType(otpRequestDto.getIndividualId()).getType();
				otpRequestDto.setIndividualIdType(idType);
				otpRequestValidator.validateIdvId(otpRequestDto.getIndividualId(), idType, errors, IdAuthCommonConstants.IDV_ID);
				DataValidationUtil.validate(errors);
				OtpResponseDTO otpResponseDTO = otpService.generateOtp(otpRequestDto, IdAuthCommonConstants.INTERNAL, requestWithMetadata);
				logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
						otpResponseDTO.getResponseTime());
				
				boolean status = otpResponseDTO.getErrors() == null || otpResponseDTO.getErrors().isEmpty();
				auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getTransactionID(),
						IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), "Internal OTP Request status : " + status);
				return otpResponseDTO;
			} catch (IDDataValidationException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
						e.getErrorText());
				auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getTransactionID(),
						IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), e);
				IdaRequestResponsConsumerUtil.setIdVersionToObjectWithMetadata(requestWithMetadata, e);
				e.putMetadata(IdAuthCommonConstants.TRANSACTION_ID, otpRequestDto.getTransactionID());
				throw authTransactionHelper.createDataValidationException(authTxnBuilder, e, requestWithMetadata);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
				auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getTransactionID(),
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