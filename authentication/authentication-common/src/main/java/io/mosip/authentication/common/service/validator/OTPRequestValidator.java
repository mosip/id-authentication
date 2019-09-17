package io.mosip.authentication.common.service.validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Dinesh Karuppiah.T
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator extends IdAuthValidator {

	private static final String OTP_CHANNEL = "otpChannel";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPRequestValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		if (Objects.nonNull(target)) {
			OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

			validateReqTime(otpRequestDto.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);

			validateTxnId(otpRequestDto.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);

			if (!errors.hasErrors()) {
				validateRequestTimedOut(otpRequestDto.getRequestTime(), errors);
			}

			if (!errors.hasErrors()) {
				validateId(otpRequestDto.getId(), errors);

				validateIdvId(otpRequestDto.getIndividualId(), IdType.getIDTypeStrOrSameStr(otpRequestDto.getIndividualIdType()), errors,
						IdAuthCommonConstants.IDV_ID);
			}

			if (!errors.hasErrors()) {
				validateOtpChannel(otpRequestDto.getOtpChannel(), errors);
			}

		}
	}

	private void validateOtpChannel(List<String> otpChannel, Errors errors) {
		if (otpChannel == null || otpChannel.isEmpty() || otpChannel.get(0).isEmpty()) {
			errors.reject(IdAuthenticationErrorConstants.OTP_CHANNEL_NOT_PROVIDED.getErrorCode(),
					IdAuthenticationErrorConstants.OTP_CHANNEL_NOT_PROVIDED.getErrorMessage());
		} else {
			String channels = otpChannel.stream()
					.filter(channel -> !NotificationType.getNotificationTypeForChannel(channel).isPresent())
					.collect(Collectors.joining(","));
			if (!channels.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE,
						IdAuthCommonConstants.INVALID_INPUT_PARAMETER + "otpChannel - ".concat(channels));
				errors.rejectValue(OTP_CHANNEL,
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new String[] {"otpChannel - ".concat(channels)},
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

}
