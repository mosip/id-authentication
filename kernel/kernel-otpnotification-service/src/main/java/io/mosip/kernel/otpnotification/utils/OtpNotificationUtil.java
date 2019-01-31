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
import io.mosip.kernel.otpnotification.constant.OtpNotificationErrorConstant;
import io.mosip.kernel.otpnotification.constant.OtpNotificationPropertyConstant;
import io.mosip.kernel.otpnotification.dto.NotifierResponseDto;
import io.mosip.kernel.otpnotification.dto.OtpRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpResponseDto;
import io.mosip.kernel.otpnotification.dto.SmsRequestDto;
import io.mosip.kernel.otpnotification.exception.OtpNotifierServiceException;

/**
 * Utils class for OTP Notification.
 * 
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

	/**
	 * This method merge template with otp provided.
	 * 
	 * @param otp
	 *            the otp generated.
	 * @param template
	 *            the template provided.
	 * @return the merged template.
	 */
	public String templateMerger(String otp, String template, String notificationType) {

		String otpTemplatePlaceholder = OtpNotificationPropertyConstant.NOTIFICATION_OTP_TEMPLATE_PLACEHOLDER
				.getProperty();
		if (!template.contains(otpTemplatePlaceholder)) {
			throw new OtpNotifierServiceException(
					OtpNotificationErrorConstant.NOTIFIER_TEMPLATE_MERGER_ERROR.getErrorCode(),
					notificationType + OtpNotificationErrorConstant.NOTIFIER_TEMPLATE_MERGER_ERROR.getErrorMessage());
		}

		Map<String, Object> templateValues = new HashMap<>();
		templateValues.put(OtpNotificationPropertyConstant.NOTIFICATION_TEMPLATE_OTP_VALUE.getProperty(), otp);

		InputStream templateInputStream = new ByteArrayInputStream(template.getBytes(Charset.forName("UTF-8")));

		InputStream resultedTemplate = null;
		try {
			resultedTemplate = templateManager.merge(templateInputStream, templateValues);
			template = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());

		} catch (IOException e) {
			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_IO_ERROR.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_IO_ERROR.getErrorMessage());
		}

		return template;
	}

	/**
	 * This method send SMS notification to the number provided with given template.
	 * 
	 * @param number
	 *            the mobile number.
	 * @param smsTemplate
	 *            the sms template provided.
	 */
	public void sendSmsNotification(String number, String smsTemplate) {
		SmsRequestDto smsRequest = new SmsRequestDto();

		smsRequest.setNumber(number);

		smsRequest.setMessage(smsTemplate);

		HttpHeaders smsHeaders = new HttpHeaders();
		smsHeaders.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SmsRequestDto> smsEntity = new HttpEntity<>(smsRequest, smsHeaders);

		restTemplate.exchange(smsServiceApi, HttpMethod.POST, smsEntity, NotifierResponseDto.class);
	}

	/**
	 * This method send email notification to the emailid provided with given
	 * template.
	 * 
	 * @param emailId
	 *            the email id provided.
	 * @param emailBodyTemplate
	 *            the email body template provided.
	 * @param emailSubjectTemplate
	 *            the email subject template.
	 */
	public void sendEmailNotification(String emailId, String emailBodyTemplate, String emailSubjectTemplate) {
		HttpHeaders emailHeaders = new HttpHeaders();
		emailHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add(OtpNotificationPropertyConstant.NOTIFICATION_EMAIL_CC.getProperty(), emailBodyTemplate);
		map.add(OtpNotificationPropertyConstant.NOTIFICATION_EMAIL_TO.getProperty(), emailId);
		map.add(OtpNotificationPropertyConstant.NOTIFICATION_EMAIL_SUBJECT.getProperty(), emailSubjectTemplate);
		map.add(OtpNotificationPropertyConstant.NOTIFICATION_EMAIL_CONTENT.getProperty(), emailBodyTemplate);
		HttpEntity<MultiValueMap<String, Object>> emailEntity = new HttpEntity<>(map, emailHeaders);

		restTemplate.exchange(emailServiceApi, HttpMethod.POST, emailEntity, NotifierResponseDto.class);
	}

	/**
	 * This method generate OTP agains provided key.
	 * 
	 * @param request
	 *            the dto with key.
	 * @return the generated OTP.
	 */
	public String generateOtp(OtpRequestDto request) {

		ResponseEntity<OtpResponseDto> response = null;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<OtpRequestDto> entity = new HttpEntity<>(request, headers);

		response = restTemplate.exchange(otpServiceApi, HttpMethod.POST, entity, OtpResponseDto.class);

		return response.getBody().getOtp();
	}

	/**
	 * This method provide key as per notification channel type mentions.
	 * 
	 * @param notificationflag
	 *            the notification types.
	 * @param number
	 *            the mobile number of user.
	 * @param emailId
	 *            the email id of user.
	 * @return the key.
	 */
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

	/**
	 * This method validates notification channel type is valid or not.
	 * 
	 * @param types
	 *            the notification channel type.
	 * @return the true if type is valid.
	 */
	public boolean containsNotificationTypes(String types) {
		if (!types.equalsIgnoreCase(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())
				&& !types.equalsIgnoreCase(OtpNotificationPropertyConstant.NOTIFICATIPON_TYPE_EMAIL.getProperty())) {

			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_INVALID_TYPE.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_INVALID_TYPE.getErrorMessage());

		}
		return true;
	}

}
