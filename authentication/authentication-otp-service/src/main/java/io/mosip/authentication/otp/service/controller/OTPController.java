package io.mosip.authentication.otp.service.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.otp.service.validator.OTPRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code OTPAuthController} use to send request to generate otp.
 * 
 * @author Rakesh Roshan
 */
@RestController
public class OTPController {

	private static final String GENERATE_OTP = "generateOTP";

	private static Logger logger = IdaLogger.getLogger(OTPController.class);

	@Autowired
	private OTPService otpService;

	@Autowired
	private OTPRequestValidator otpRequestValidator;

	@Autowired
	private Environment env;

	/** The Id Info Fetcher */
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

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
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException the id authentication app exception
	 * @throws IDDataValidationException
	 */
	@PostMapping(path = "/{Auth-Partner-ID}/{MISP-LK}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, @ApiIgnore Errors errors,
			@PathVariable("Auth-Partner-ID") String partnerId, @PathVariable("MISP-LK") String mispLK)
			throws IdAuthenticationAppException, IDDataValidationException {
		OtpResponseDTO otpResponseDTO = null;
		try {
			DataValidationUtil.validate(errors);
			otpResponseDTO = otpService.generateOtp(otpRequestDto, partnerId);
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
					otpResponseDTO.getResponseTime());
			return otpResponseDTO;
		} catch (IDDataValidationException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), GENERATE_OTP,
					e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		} finally {
			boolean isStatus = otpResponseDTO != null && otpResponseDTO.getErrors() != null
					&& !otpResponseDTO.getErrors().isEmpty();
			IdType actualidType = null;
			String idType = otpRequestDto.getIndividualIdType();
			if (idType != null && !idType.isEmpty()) {
				actualidType = idType.equalsIgnoreCase(IdType.UIN.getType()) ? IdType.UIN : IdType.VID;
			}
			logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
					AUTH_FACADE, "OTP Request status : " + isStatus);
			auditHelper.audit(AuditModules.OTP_REQUEST, AuditEvents.AUTH_REQUEST_RESPONSE,
					otpRequestDto.getId() != null && !otpRequestDto.getId().isEmpty() ? otpRequestDto.getId() : "",
					actualidType, AuditModules.OTP_REQUEST.getDesc());
		}
	}

}
