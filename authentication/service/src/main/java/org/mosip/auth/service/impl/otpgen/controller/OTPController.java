package org.mosip.auth.service.impl.otpgen.controller;

import javax.validation.Valid;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.auth.core.util.DataValidationUtil;
import org.mosip.auth.service.impl.otpgen.validator.OTPRequestValidator;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code OTPAuthController} use to send request to generate otp.
 * 
 * @author Rakesh Roshan
 */
@RestController
public class OTPController {

	private MosipLogger LOGGER;

	@Autowired
	OTPFacade otpFacade;

	@Autowired
	private OTPRequestValidator otpRequestValidator;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

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
	 * @throws IdAuthenticationAppException
	 */
	@PostMapping(path = "/otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, Errors errors)
			throws IdAuthenticationAppException {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (errors.hasErrors()) {
			try {
				DataValidationUtil.validate(errors);
			} catch (IDDataValidationException e) {
				LOGGER.error("sessionId", null, null, e.getErrorText());
				throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
			}
		} else {
			try {
				otpResponseDTO = otpFacade.generateOtp(otpRequestDto);
				LOGGER.info("sessionId", "NA", "NA", "NA");
			} catch (IdAuthenticationBusinessException e) {
				LOGGER.error("sessionId", e.getClass().toString(), e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}
		}
		return otpResponseDTO;
	}

}
