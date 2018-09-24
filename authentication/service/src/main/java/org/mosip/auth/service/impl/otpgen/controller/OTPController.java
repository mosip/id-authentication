package org.mosip.auth.service.impl.otpgen.controller;

import java.util.Date;

import javax.validation.Valid;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.OtpRequestDTO;
import org.mosip.auth.core.dto.indauth.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.auth.service.impl.otpgen.validator.OTPRequestValidator;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
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
//@RequestMapping(value = "/v0.1/id-auth/otp-gen")
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
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, BindingResult result)
			throws IdAuthenticationAppException {

		if (result.hasErrors()) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_GENERATE_OTP_REQUEST);
		} else {
			try {
				OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
				if (otpFacade.generateOtp(otpRequestDto)) {

					otpResponseDTO.setStatus("OTP_GENERATED");
					otpResponseDTO.setResponseTime(new Date());

				} else {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED);
				}
				LOGGER.info("sessionId", "NA", "NA", "NA");
				return otpResponseDTO;
			} catch (IdAuthenticationBusinessException e) {
				LOGGER.error("sessionId", e.getClass().toString(), e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}

		}

	}

}
