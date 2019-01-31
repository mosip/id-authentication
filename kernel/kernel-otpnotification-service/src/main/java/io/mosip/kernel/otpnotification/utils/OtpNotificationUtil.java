package io.mosip.kernel.otpnotification.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.otpnotification.constant.OtpNotificationPropertyConstant;
import io.mosip.kernel.otpnotification.dto.NotifierResponseDto;
import io.mosip.kernel.otpnotification.dto.OtpRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpResponseDto;
import io.mosip.kernel.otpnotification.dto.SmsRequestDto;
import io.mosip.kernel.otpnotification.exception.OtpNotifierServiceException;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Component
public class OtpNotificationUtil {

	/**
	 * OTP generator service api.
	 */
	@Value("${mosip.kernel.otpnotification.otp.api}")
	private String otpServiceApi;

	/**
	 * Sms service api.
	 */
	@Value("${mosip.kernel.otpnotification.sms.api}")
	private String smsServiceApi;

	/**
	 * Email service api.
	 */
	@Value("${mosip.kernel.otpnotification.email.api}")
	private String emailServiceApi;

	/**
	 * Reference to TemplateManager.
	 */
	@Autowired
	private TemplateManager templateManager;

	/**
	 * Reference to rest template.
	 */
	@Autowired
	private RestTemplate restTemplate;

	public String templateMerger(String otp, String template) {

		Map<String, Object> templateValues = new HashMap<>();
		templateValues.put("otp", otp);

		InputStream templateInputStream = new ByteArrayInputStream(template.getBytes(Charset.forName("UTF-8")));

		InputStream resultedTemplate = null;
		try {
			resultedTemplate = templateManager.merge(templateInputStream, templateValues);
			template = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());

		} catch (IOException e) {
			throw new OtpNotifierServiceException("xxxxx", "Input output error occur during email template merging");
		}

		return template;
	}

	public void sendSmsNotification(String number, String smsTemplate) {
		SmsRequestDto smsRequest = new SmsRequestDto();

		smsRequest.setNumber(number);

		smsRequest.setMessage(smsTemplate);

		HttpHeaders smsHeaders = new HttpHeaders();
		smsHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SmsRequestDto> smsEntity = new HttpEntity<>(smsRequest, smsHeaders);

		restTemplate.exchange(smsServiceApi, HttpMethod.POST, smsEntity, NotifierResponseDto.class);
	}

	public void sendEmailNotification(String emailId, String emailBodyTemplate, String emailSubjectTemplate) {
		HttpHeaders emailHeaders = new HttpHeaders();
		emailHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<Object, Object> map = new LinkedMultiValueMap<>();
		map.add("mailCC", emailBodyTemplate);
		map.add("mailTo", emailId);
		map.add("mailSubject", emailSubjectTemplate);
		map.add("mailContent", emailBodyTemplate);
		HttpEntity<MultiValueMap<Object, Object>> emailEntity = new HttpEntity<>(map, emailHeaders);

		restTemplate.exchange(emailServiceApi, HttpMethod.POST, emailEntity, NotifierResponseDto.class);
	}

	public String generateOtp(OtpRequestDto request) {

		ResponseEntity<OtpResponseDto> response = null;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<OtpRequestDto> entity = new HttpEntity<>(request, headers);

		response = restTemplate.exchange(otpServiceApi, HttpMethod.POST, entity, OtpResponseDto.class);

		return response.getBody().getOtp();
	}

	public String getKey(List<String> notificationflag, String number, String emailId) {

		String key = null;

		if (notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())
				&& notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATIPON_TYPE_EMAIL.getProperty())) {

			key = number + emailId;

		} else {

			if (notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())) {
				key = number;
			}

			if (notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATIPON_TYPE_EMAIL.getProperty())) {
				key = emailId;
			}
		}
		return key;
	}

}
