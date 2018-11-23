package io.mosip.kernel.smsnotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.notification.spi.SmsNotification;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.smsnotification.constant.SmsExceptionConstant;
import io.mosip.kernel.smsnotification.constant.SmsPropertyConstant;
import io.mosip.kernel.smsnotification.dto.SmsResponseDto;
import io.mosip.kernel.smsnotification.dto.SmsServerResponseDto;
import io.mosip.kernel.smsnotification.exception.InvalidNumberException;

/**
 * This service class send SMS on the contact number provided.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class SmsNotificationServiceImpl implements SmsNotification<SmsResponseDto>{

	/**
	 * The reference that autowired rest template builder.
	 */
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${mosip.kernel.sms.api}")
	String api;

	@Value("${mosip.kernel.sms.authkey}")
	String authkey;

	@Value("${mosip.kernel.sms.country.code}")
	String countryCode;

	@Value("${mosip.kernel.sms.sender}")
	String senderId;

	@Value("${mosip.kernel.sms.route}")
	String route;

	@Value("${mosip.kernel.sms.number.length}")
	String length;

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.notification.spi.SmsNotification#sendSmsNotification(java.lang.String, java.lang.String)
	 */
	@Override
	public SmsResponseDto sendSmsNotification(String contactNumber, String contentMessage)  {

		if (!StringUtils.isNumeric(contactNumber) || contactNumber.length() < Integer.parseInt(length)
				|| contactNumber.length() > Integer.parseInt(length)) {
			throw new InvalidNumberException(SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
					SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorMessage());
		}

		RestTemplate restTemplate = restTemplateBuilder.build();
		SmsResponseDto result = new SmsResponseDto();
		ResponseEntity<SmsServerResponseDto> response = null;
		UriComponentsBuilder sms = UriComponentsBuilder.fromHttpUrl(api)
				.queryParam(SmsPropertyConstant.AUTH_KEY.getProperty(), authkey)
				.queryParam(SmsPropertyConstant.SMS_MESSAGE.getProperty(), contentMessage)
				.queryParam(SmsPropertyConstant.ROUTE.getProperty(), route)
				.queryParam(SmsPropertyConstant.SENDER_ID.getProperty(), senderId)
				.queryParam(SmsPropertyConstant.RECIPIENT_NUMBER.getProperty(), contactNumber)
				.queryParam(SmsPropertyConstant.COUNTRY_CODE.getProperty(), countryCode);

		response = restTemplate.getForEntity(sms.toUriString(), SmsServerResponseDto.class);

		if (response.getBody().getType().equals(SmsPropertyConstant.VENDOR_RESPONSE_SUCCESS.getProperty())) {
			result.setMessage(SmsPropertyConstant.SUCCESS_RESPONSE.getProperty());
			result.setStatus(response.getBody().getType());
		}
		return result;

	}

}
