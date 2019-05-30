package io.mosip.kernel.smsnotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.notification.spi.SmsNotification;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.smsnotification.constant.SmsExceptionConstant;
import io.mosip.kernel.smsnotification.constant.SmsPropertyConstant;
import io.mosip.kernel.smsnotification.dto.SmsResponseDto;
import io.mosip.kernel.smsnotification.dto.SmsVendorRequestDto;
import io.mosip.kernel.smsnotification.exception.InvalidNumberException;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RefreshScope
@Service
public class SmsNotificationServiceImpl implements SmsNotification<SmsResponseDto> {

	/**
	 * The reference that autowired rest template builder.
	 */
	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.kernel.sms.api}")
	String api;

	@Value("${mosip.kernel.sms.username}")
	String username;

	@Value("${mosip.kernel.sms.password}")
	private String password;

	@Value("${mosip.kernel.sms.country.code}")
	String countryCode;

	@Value("${mosip.kernel.sms.sender}")
	String senderId;

	@Value("${mosip.kernel.sms.route}")
	String route;

	@Value("${mosip.kernel.sms.number.length}")
	String length;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.notification.spi.SmsNotification#sendSmsNotification(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public SmsResponseDto sendSmsNotification(String contactNumber, String contentMessage) {

		if (!StringUtils.isNumeric(contactNumber) || contactNumber.length() < Integer.parseInt(length)
				|| contactNumber.length() > Integer.parseInt(length)) {
			throw new InvalidNumberException(SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
					SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorMessage() + length
							+ SmsPropertyConstant.SUFFIX_MESSAGE.getProperty());
		}
		HttpHeaders headers = new HttpHeaders();
		String authorization = username + ":" + password;
		String authKey = "Basic " + CryptoUtil.encodeBase64String(authorization.getBytes());
		headers.add("authorization", authKey);
		SmsVendorRequestDto vendorRequestDto = new SmsVendorRequestDto();
		vendorRequestDto.setFrom(senderId);
		vendorRequestDto.setText(contentMessage);
		vendorRequestDto.setTo(countryCode + contactNumber);
		HttpEntity<SmsVendorRequestDto> httpEntity = new HttpEntity<>(vendorRequestDto, headers);
		restTemplate.postForEntity(api, httpEntity, Object.class);
		SmsResponseDto result = new SmsResponseDto();
		result.setMessage(SmsPropertyConstant.SUCCESS_RESPONSE.getProperty());
		result.setStatus("success");
		return result;

	}

}
