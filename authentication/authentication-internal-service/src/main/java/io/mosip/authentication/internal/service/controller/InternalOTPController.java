package io.mosip.authentication.internal.service.controller;

import java.util.Objects;
import java.util.Optional;

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
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.internal.service.validator.InternalOTPRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code InternalOTPController} use to send request to generate otp.
 * 
 * @author Dinesh Karuppiah.T
 */
@RestController
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
	@PreAuthorize("hasAnyRole('RESIDENT')")
	@PostMapping(path = "/otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, @ApiIgnore Errors errors)
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
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
			OtpResponseDTO otpResponseDTO = otpService.generateOtp(otpRequestDto, IdAuthCommonConstants.INTERNAL);
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
					otpResponseDTO.getResponseTime());
			
			boolean status = otpResponseDTO.getErrors() == null || otpResponseDTO.getErrors().isEmpty();
			auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getIndividualId(),
					IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), "Internal OTP Request status : " + status);
			return otpResponseDTO;
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
					e.getErrorText());
			auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getIndividualId(),
					IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), e);
			throw authTransactionHelper.createDataValidationException(authTxnBuilder, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE, otpRequestDto.getIndividualId(),
					IdType.getIDTypeOrDefault(otpRequestDto.getIndividualIdType()), e);
			IdAuthenticationAppException authenticationAppException = new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			authTransactionHelper.setAuthTransactionEntityMetadata(authenticationAppException, authTxnBuilder);
			throw authenticationAppException;
		}
	}

}
