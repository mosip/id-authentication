package io.mosip.kernel.notifier.sms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.spi.smsnotifier.SmsNotifier;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.notifier.sms.constant.SmsExceptionConstants;
import io.mosip.kernel.notifier.sms.constant.SmsPropertyConstants;
import io.mosip.kernel.notifier.sms.dto.SmsResponseDto;
import io.mosip.kernel.notifier.sms.dto.SmsServerResponseDto;
import io.mosip.kernel.notifier.sms.exception.MosipInvalidNumberException;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
@PropertySource(value = { "application.properties" })
public class SmsNotifierServiceImpl implements SmsNotifier<SmsResponseDto> {

	/**
	 * The reference that autowired rest template builder.
	 */
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${sms.api}")
	String api;

	@Value("${sms.authkey}")
	String authkey;

	@Value("${sms.country.code}")
	String countryCode;

	@Value("${sms.sender}")
	String senderId;

	@Value("${sms.route}")
	String route;

	@Value("${sms.number.length}")
	String length;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.smsnotifier.SmsNotifier#sendSmsNotification(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public SmsResponseDto sendSmsNotification(String contactNumber, String contentMessage) {

		if (!StringUtils.isNumeric(contactNumber) || contactNumber.length() < Integer.parseInt(length)
				|| contactNumber.length() > Integer.parseInt(length)) {
			throw new MosipInvalidNumberException(SmsExceptionConstants.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
					SmsExceptionConstants.SMS_INVALID_CONTACT_NUMBER.getErrorMessage());
		}

		RestTemplate restTemplate = restTemplateBuilder.build();
		SmsResponseDto result = new SmsResponseDto();
		ResponseEntity<SmsServerResponseDto> response = null;
		UriComponentsBuilder sms = UriComponentsBuilder.fromHttpUrl(api)
				.queryParam(SmsPropertyConstants.AUTH_KEY.getProperty(), authkey)
				.queryParam(SmsPropertyConstants.SMS_MESSAGE.getProperty(), contentMessage)
				.queryParam(SmsPropertyConstants.ROUTE.getProperty(), route)
				.queryParam(SmsPropertyConstants.SENDER_ID.getProperty(), senderId)
				.queryParam(SmsPropertyConstants.RECIPIENT_NUMBER.getProperty(), contactNumber)
				.queryParam(SmsPropertyConstants.COUNTRY_CODE.getProperty(), countryCode);

		response = restTemplate.getForEntity(sms.toUriString(), SmsServerResponseDto.class);

		if (response.getBody().getType().equals(SmsPropertyConstants.VENDOR_RESPONSE_SUCCESS.getProperty())) {
			result.setMessage(SmsPropertyConstants.SUCCESS_RESPONSE.getProperty());
			result.setStatus(response.getBody().getType());
		}
		return result;

	}

}
