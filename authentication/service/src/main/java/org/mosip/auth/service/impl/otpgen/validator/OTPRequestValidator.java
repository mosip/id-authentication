package org.mosip.auth.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.indauth.OtpRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator implements Validator {

	@Autowired
	private SpringValidatorAdapter validator;

	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

		validator.validate(otpRequestDto, errors);
		//FIXME
		/*if (!isTimestampValid(otpRequestDto.getRequestTime())) {
			errors.rejectValue("requestTime", IdAuthenticationErrorConstants.EXPIRED_OTP_REQUEST_TIME.getErrorCode(),
					IdAuthenticationErrorConstants.EXPIRED_OTP_REQUEST_TIME.getErrorMessage());
		}*/

		if (!otpRequestDto.getIdType().UIN.getType().equals(IDType.UIN.getType())
				|| !otpRequestDto.getIdType().VID.getType().equals(IDType.VID.getType())) {

			errors.rejectValue("idType", IdAuthenticationErrorConstants.INVALID_IDTYPE.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_IDTYPE.getErrorMessage());
		}

	}

	private boolean isTimestampValid(Date timestamp) {
		Date reqTime = timestamp;
		Instant reqTimeInstance = reqTime.toInstant();
		Instant now = Instant.now();

		if (Duration.between(reqTimeInstance, now).get(ChronoUnit.MINUTES) < 20) {
			return true;
		} else {
			return false;
		}

	}
}
