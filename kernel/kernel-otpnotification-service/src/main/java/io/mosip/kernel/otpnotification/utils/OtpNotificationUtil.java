package io.mosip.kernel.otpnotification.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.otpnotification.constant.OtpNotificationErrorConstant;
import io.mosip.kernel.otpnotification.constant.OtpNotificationPropertyConstant;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpRequestDto;
import io.mosip.kernel.otpnotification.dto.SmsRequestDto;
import io.mosip.kernel.otpnotification.exception.OtpNotificationInvalidArgumentException;
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
	 * Reference to {@link TemplateManager}.
	 */
	@Autowired
	private TemplateManager templateManager;

	/**
	 * Reference to {@link RestTemplate}.
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Reference to ObjectMapper.
	 */
	@Autowired
	private ObjectMapper mapper;

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
		templateValues.put(OtpNotificationPropertyConstant.NOTIFICATION_OTP_VALUE.getProperty(), otp);

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

		ResponseEntity<String> response = restTemplate.exchange(smsServiceApi, HttpMethod.POST, smsEntity,
				String.class);

		String responseBody = response.getBody();

		List<ServiceError> validationErrorsList = null;
		try {
			validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		} catch (IOException e) {
			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_SMS_IO_ERROR.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_SMS_IO_ERROR.getErrorMessage());
		}

		if (!validationErrorsList.isEmpty()) {
			throw new OtpNotificationInvalidArgumentException(validationErrorsList);
		}

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

		ResponseEntity<String> response = restTemplate.exchange(emailServiceApi, HttpMethod.POST, emailEntity,
				String.class);

		String responseBody = response.getBody();

		List<ServiceError> validationErrorsList = null;
		try {
			validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		} catch (IOException e) {
			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_EMAIL_IO_ERROR.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_EMAIL_IO_ERROR.getErrorMessage());
		}

		if (!validationErrorsList.isEmpty()) {
			throw new OtpNotificationInvalidArgumentException(validationErrorsList);
		}
	}

	/**
	 * This method generate OTP agains provided key.
	 * 
	 * @param request
	 *            the dto with key.
	 * @return the generated OTP.
	 */
	public String generateOtp(OtpRequestDto request) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<OtpRequestDto> entity = new HttpEntity<>(request, headers);

		ResponseEntity<String> response = restTemplate.exchange(otpServiceApi, HttpMethod.POST, entity, String.class);

		String responseBody = response.getBody();

		List<ServiceError> validationErrorsList = null;
		try {
			validationErrorsList = ExceptionUtils.getServiceErrorList(responseBody);
		} catch (IOException e1) {
			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_OTP_IO_ERROR.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_OTP_IO_ERROR.getErrorMessage());
		}

		if (!validationErrorsList.isEmpty()) {
			throw new OtpNotificationInvalidArgumentException(validationErrorsList);
		}

		JsonNode otpResponse = null;
		String otp = null;
		try {
			otpResponse = mapper.readTree(responseBody);

			otp = otpResponse.get(OtpNotificationPropertyConstant.NOTIFICATION_OTP_VALUE.getProperty()).asText();

		} catch (IOException e) {
			throw new OtpNotifierServiceException(
					OtpNotificationErrorConstant.NOTIFIER_OTP_IO_RETRIVAL_ERROR.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_OTP_IO_RETRIVAL_ERROR.getErrorMessage());
		}

		return otp;
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
				&& notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_EMAIL.getProperty())) {

			key = number + emailId;

		} else {

			if (notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())) {
				key = number;
			}

			if (notificationflag.contains(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_EMAIL.getProperty())) {
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
				&& !types.equalsIgnoreCase(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_EMAIL.getProperty())) {

			throw new OtpNotifierServiceException(OtpNotificationErrorConstant.NOTIFIER_INVALID_TYPE.getErrorCode(),
					OtpNotificationErrorConstant.NOTIFIER_INVALID_TYPE.getErrorMessage());

		}
		return true;
	}

	/**
	 * This method validate request dto with valid notification types mention.
	 * 
	 * @param request
	 *            the request dto for OTP notification.
	 * @return the list of {@link ServiceError}.
	 */
	public List<ServiceError> validationRequestArguments(OtpNotificationRequestDto request) {

		List<ServiceError> validationErrorsList = new ArrayList<>();

		for (int type = 0; type < request.getNotificationTypes().size(); type++) {

			if (request.getNotificationTypes().get(type)
					.equals(OtpNotificationPropertyConstant.NOTIFICATION_TYPE_SMS.getProperty())) {

				if (request.getSmsTemplate() == null || request.getSmsTemplate().isEmpty()) {
					validationErrorsList.add(
							new ServiceError(OtpNotificationErrorConstant.NOTIFIER_SMS_TEMPLATE_ERROR.getErrorCode(),
									OtpNotificationErrorConstant.NOTIFIER_SMS_TEMPLATE_ERROR.getErrorMessage()));
				}
				if (request.getMobileNumber() == null || request.getMobileNumber().isEmpty()) {
					validationErrorsList
							.add(new ServiceError(OtpNotificationErrorConstant.NOTIFIER_SMS_NUMBER_ERROR.getErrorCode(),
									OtpNotificationErrorConstant.NOTIFIER_SMS_NUMBER_ERROR.getErrorMessage()));
				}

			} else {

				if (request.getEmailId() == null || request.getEmailId().isEmpty()) {
					validationErrorsList
							.add(new ServiceError(OtpNotificationErrorConstant.NOTITFIER_EMAIL_ID_ERROR.getErrorCode(),
									OtpNotificationErrorConstant.NOTITFIER_EMAIL_ID_ERROR.getErrorMessage()));
				}
				if (request.getEmailSubjectTemplate() == null || request.getEmailSubjectTemplate().isEmpty()) {
					validationErrorsList.add(
							new ServiceError(OtpNotificationErrorConstant.NOTITFIER_EMAIL_SUBJECT_ERROR.getErrorCode(),
									OtpNotificationErrorConstant.NOTITFIER_EMAIL_SUBJECT_ERROR.getErrorMessage()));
				}
				if (request.getEmailBodyTemplate() == null || request.getEmailBodyTemplate().isEmpty()) {
					validationErrorsList.add(new ServiceError(
							OtpNotificationErrorConstant.NOTIFIER_EMAIL_BODY_TEMPLATE_ERROR.getErrorCode(),
							OtpNotificationErrorConstant.NOTIFIER_EMAIL_BODY_TEMPLATE_ERROR.getErrorMessage()));
				}

			}

		}
		return validationErrorsList;
	}

}
