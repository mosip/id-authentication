package io.mosip.authentication.service.impl.otpgen.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code OTPAuthController} use to send request to generate otp.
 * 
 * @author Rakesh Roshan
 */
@RestController
public class OTPController {

	private static final String DEAFULT_SESSION_ID = "sessionId";

	private static Logger logger = IdaLogger.getLogger(OTPController.class);

	@Autowired
	private OTPFacade otpFacade;

	@Autowired
	private OTPRequestValidator otpRequestValidator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(otpRequestValidator);
	}

	/**
	 * send OtpRequestDTO request to generate OTP and received OtpResponseDTO as
	 * output.
	 * 
	 * @param otpRequestDto
	 *            as request body
	 * @param errors
	 *            associate error
	 * @return otpResponseDTO
	 * @throws IdAuthenticationAppException
	 */
	@PostMapping(path = "/v1.0/otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public OtpResponseDTO generateOTP(@Valid @RequestBody OtpRequestDTO otpRequestDto, @ApiIgnore Errors errors)
			throws IdAuthenticationAppException {

		try {
			DataValidationUtil.validate(errors);
			OtpResponseDTO otpResponseDTO  = otpFacade.generateOtp(otpRequestDto);
			logger.info(DEAFULT_SESSION_ID, "NA", "NA", "NA");
			return otpResponseDTO;
		} catch (IDDataValidationException e) {
			logger.error(DEAFULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(DEAFULT_SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}

}
