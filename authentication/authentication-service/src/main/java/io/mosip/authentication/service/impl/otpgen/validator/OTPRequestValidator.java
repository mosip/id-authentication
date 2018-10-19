package io.mosip.authentication.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uinvalidator.UinValidator;
import io.mosip.kernel.idvalidator.vidvalidator.VidValidator;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator implements Validator {

	private static final String VALIDATE = "validate";
	private static final String AUTH_REQUEST_VALIDATOR = "OTPValidator";
	private static final String SESSION_ID = "sessionId";

	private MosipLogger mosipLogger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		mosipLogger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	@Autowired
	private SpringValidatorAdapter validator;

	@Autowired
	private Environment env;

	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

		validator.validate(otpRequestDto, errors);

		String idType = otpRequestDto.getIdType();

		if (idType.equals(IdType.UIN.getType())) {
			try {
				UinValidator uinValidator = new UinValidator();
				uinValidator.validateId(otpRequestDto.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (idType.equals(IdType.VID.getType())) {
			try {
				VidValidator vidValidator = new VidValidator();
				vidValidator.validateId(otpRequestDto.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "INCORRECT_IDTYPE - " + idType);
			errors.rejectValue("idType", IdAuthenticationErrorConstants.INCORRECT_IDTYPE.getErrorCode(),
					env.getProperty("mosip.ida.validation.message.AuthRequest.Idtype"));
		}

		if (!isTimestampValid(otpRequestDto.getReqTime())) {
			errors.rejectValue("reqTime",
					IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
		}

	}

	private boolean isTimestampValid(Date timestamp) {

		Date reqTime = (Date) timestamp.clone();
		Instant reqTimeInstance = reqTime.toInstant();
		Instant now = Instant.now();

		return Duration.between(reqTimeInstance, now).toMinutes() < env.getProperty("requestdate.received.in.max.time",
				Integer.class);

	}
}
