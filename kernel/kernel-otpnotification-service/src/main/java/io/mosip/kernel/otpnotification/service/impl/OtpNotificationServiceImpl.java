package io.mosip.kernel.otpnotification.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.otpnotification.spi.OtpNotification;
import io.mosip.kernel.otpnotification.constant.OtpNotificationPropertyConstant;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpNotificationResponseDto;
import io.mosip.kernel.otpnotification.dto.OtpRequestDto;
import io.mosip.kernel.otpnotification.exception.OtpNotificationInvalidArgumentException;
import io.mosip.kernel.otpnotification.utils.OtpNotificationUtil;

/**
 * Service class to send OTP notification to user.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Service
public class OtpNotificationServiceImpl
		implements OtpNotification<OtpNotificationResponseDto, OtpNotificationRequestDto> {

	/**
	 * Reference to {@link OtpNotificationUtil}.
	 */
	@Autowired
	private OtpNotificationUtil notificationUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.otpnotification.spi.OtpNotification#sendOtpNotification(
	 * java.lang.Object)
	 */
	@Override
	public OtpNotificationResponseDto sendOtpNotification(OtpNotificationRequestDto requestDto) {

		OtpNotificationResponseDto responseDto = new OtpNotificationResponseDto();

		OtpRequestDto request = new OtpRequestDto();

		requestDto.getNotificationTypes().replaceAll(String::toLowerCase);

		requestDto.getNotificationTypes().forEach(notificationUtil::containsNotificationTypes);

		List<ServiceError> validationListError = notificationUtil.validationRequestArguments(requestDto);

		if (!validationListError.isEmpty()) {
			throw new OtpNotificationInvalidArgumentException(validationListError);
		}

		request.setKey(notificationUtil.getKey(requestDto.getNotificationTypes(), requestDto.getMobileNumber(),
				requestDto.getEmailId()));

		String otp = notificationUtil.generateOtp(request);

		for (int type = 0; type < requestDto.getNotificationTypes().size(); type++) {

			if (requestDto.getNotificationTypes().get(type)
					.equalsIgnoreCase(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())) {

				String smsTemplate = notificationUtil.templateMerger(otp, requestDto.getSmsTemplate(),
						OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty());
				notificationUtil.sendSmsNotification(requestDto.getMobileNumber(), smsTemplate);

			}
			if (requestDto.getNotificationTypes().get(type)
					.equalsIgnoreCase(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_EMAIL.getProperty())) {

				String emailBodyTemplate = notificationUtil.templateMerger(otp, requestDto.getEmailBodyTemplate(),
						OtpNotificationPropertyConstant.NOTIFICATION_TYPE_EMAIL.getProperty());
				notificationUtil.sendEmailNotification(requestDto.getEmailId(), emailBodyTemplate,
						requestDto.getEmailSubjectTemplate());

			}

		}

		responseDto.setStatus(OtpNotificationPropertyConstant.NOTIFICATION_RESPONSE_STATUS.getProperty());
		responseDto.setMessage(OtpNotificationPropertyConstant.NOTIFICATION_RESPONSE_MESSAGE.getProperty());
		return responseDto;
	}

}
