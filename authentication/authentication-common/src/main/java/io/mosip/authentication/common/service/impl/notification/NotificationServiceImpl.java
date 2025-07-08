package io.mosip.authentication.common.service.impl.notification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.KeyBindedTokenAuthType;
import io.mosip.authentication.common.service.impl.match.PasswordAuthType;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.indauth.dto.SenderType;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.util.LanguageComparator;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.kernel.core.util.DateUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/***
 * 
 * Service class to notify users via SMS or Email notification.
 * 
 * @author Dinesh Karuppiah.T
 */
@Service
public class NotificationServiceImpl implements NotificationService {

	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";
	/** The Constant NAME. */
	private static final String NAME = "name";
	/** The Constant TIME. */
	private static final String TIME = "time";
	/** The Constant DATE. */
	private static final String DATE = "date";

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** ID Template manager */
	@Autowired
	private IdTemplateManager idTemplateManager;

	@Autowired
	private NotificationManager notificationManager;
	
	@Autowired
	@Qualifier("NotificationLangComparator")
	private LanguageComparator languageComparator;

	@Autowired
	private EntityInfoUtil entityInfoUtil;

	@Autowired
	private LanguageUtil languageUtil;

	public void sendAuthNotification(AuthRequestDTO authRequestDTO, String idvid, AuthResponseDTO authResponseDTO,
									 Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException {

		Map<String, Object> values = new HashMap<>();
		List<String> templateLanguages = getTemplateLanguages(idInfo);
		System.out.println("Template Languages: " + templateLanguages);

		for (String lang : templateLanguages) {
			String nameValue = entityInfoUtil.getEntityInfoAsString(DemoMatchType.NAME, lang, idInfo);
			System.out.println("Name (" + lang + "): " + nameValue);
			values.put(NAME + "_" + lang, nameValue);
		}

		Tuple2<String, String> dateAndTime = getDateAndTime(DateUtils.parseToLocalDateTime(authResponseDTO.getResponseTime()));
		System.out.println("Auth Date: " + dateAndTime.getT1());
		System.out.println("Auth Time: " + dateAndTime.getT2());
		values.put(DATE, dateAndTime.getT1());
		values.put(TIME, dateAndTime.getT2());

		String maskedUin = "";
		String charCount = EnvUtil.getUinMaskingCharCount();
		System.out.println("UIN Masking Char Count: " + charCount);
		if (charCount != null && !charCount.isEmpty()) {
			maskedUin = MaskUtil.generateMaskValue(idvid, Integer.parseInt(charCount));
		}
		System.out.println("Masked UIN: " + maskedUin);
		values.put("idvid", maskedUin);

		String idvidType = authRequestDTO.getIndividualIdType();
		System.out.println("IDVID Type: " + idvidType);
		values.put("idvidType", idvidType);

		String authTypeStr = Stream
				.of(Stream.<AuthType>of(DemoAuthType.values()), Stream.<AuthType>of(BioAuthType.values()),
						Stream.<AuthType>of(PinAuthType.values()), Stream.<AuthType>of(PasswordAuthType.values()),
						Stream.<AuthType>of(KeyBindedTokenAuthType.values()))
				.flatMap(Function.identity())
				.filter(authType -> authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher))
				.peek(authType -> System.out.println("Enabled AuthType: " + authType.getClass().getSimpleName()))
				.map(authType -> authType.getDisplayName(authRequestDTO, idInfoFetcher))
				.peek(displayName -> System.out.println("AuthType Display Name: " + displayName))
				.distinct()
				.collect(Collectors.joining(","));
		System.out.println("Auth Types: " + authTypeStr);
		values.put(AUTH_TYPE, authTypeStr);

		boolean authStatus = authResponseDTO.getResponse().isAuthStatus();
		System.out.println("Auth Status: " + (authStatus ? "Passed" : "Failed"));
		values.put(IdAuthCommonConstants.STATUS, authStatus ? "Passed" : "Failed");

		String phoneNumber = entityInfoUtil.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
		String email = entityInfoUtil.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo);
		System.out.println("Phone Number: " + phoneNumber);
		System.out.println("Email: " + email);

		String notificationType;
		if (isAuth) {
			notificationType = EnvUtil.getNotificationType();
		} else {
			notificationType = NotificationType.NONE.getName();
		}
		System.out.println("Notification Type: " + notificationType);

		System.out.println("Final Notification Values Map: " + values);
		sendNotification(values, email, phoneNumber, SenderType.AUTH, notificationType, templateLanguages);
	}


	public void sendOTPNotification(String idvid, String idvidType, Map<String, String> valueMap,
			List<String> templateLanguages, String otp, String notificationProperty, LocalDateTime otpGenerationTime)
			throws IdAuthenticationBusinessException {
		Map<String, Object> otpTemplateValues = getOtpTemplateValues(idvid, idvidType, valueMap, otpGenerationTime);
		otpTemplateValues.put("otp", otp);
		this.sendNotification(otpTemplateValues, valueMap.get(IdAuthCommonConstants.EMAIL),
				valueMap.get(IdAuthCommonConstants.PHONE_NUMBER), SenderType.OTP, notificationProperty,
				templateLanguages);
	}

	/*
	 * Send Otp Notification
	 * 
	 */
	private Map<String, Object> getOtpTemplateValues(String idvid, String idvidType, Map<String, String> valueMap,
			LocalDateTime otpGenerationTime) {

		Tuple2<String, String> dateAndTime = getDateAndTime(otpGenerationTime);
		String date = dateAndTime.getT1();
		String time = dateAndTime.getT2();

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		String charCount = EnvUtil.getUinMaskingCharCount();
		if (charCount != null) {
			maskedUin = MaskUtil.generateMaskValue(idvid, Integer.parseInt(charCount));
		}
		values.put("idvid", maskedUin);
		values.put("idvidType", idvidType);
		Integer timeInSeconds = EnvUtil.getOtpExpiryTime();
		int timeInMinutes = (timeInSeconds % 3600) / 60;
		values.put("validTime", String.valueOf(timeInMinutes));
		values.put(DATE, date);
		values.put(TIME, time);
		values.putAll(valueMap);
		values.remove(IdAuthCommonConstants.PHONE_NUMBER);
		values.remove(IdAuthCommonConstants.EMAIL);
		return values;
	}

	/**
	 * Gets the date and time.
	 *
	 * @param requestTime the request time
	 * @param pattern     the pattern
	 * @return the date and time
	 */
	private Tuple2<String, String> getDateAndTime(LocalDateTime timestamp) {
		ZonedDateTime dateTime = ZonedDateTime.of(timestamp, ZoneId.of("UTC")).withZoneSameInstant(getZone());
		String date = dateTime.format(DateTimeFormatter.ofPattern(EnvUtil.getNotificationDateFormat()));
		String time = dateTime.format(DateTimeFormatter.ofPattern(EnvUtil.getNotificationTimeFormat()));
		return Tuples.of(date, time);
	}

	private ZoneId getZone() {
		return ZoneId.of(EnvUtil.getNotificationTimeZone());
	}

	/**
	 * Method to Send Notification to the Individual via SMS / E-Mail
	 * 
	 * @param notificationtype     - specifies notification type
	 * @param values               - list of values to send notification
	 * @param emailId              - sender E-Mail ID
	 * @param phoneNumber          - sender Phone Number
	 * @param sender               - to specify the sender type
	 * @param notificationProperty
	 * @throws IdAuthenticationBusinessException
	 */

	public void sendNotification(Map<String, Object> values, String emailId, String phoneNumber, SenderType sender,
								 String notificationProperty, List<String> templateLanguages) throws IdAuthenticationBusinessException {

		System.out.println("----- Inside sendNotification -----");
		System.out.println("Email ID: " + emailId);
		System.out.println("Phone Number: " + phoneNumber);
		System.out.println("Sender: " + sender);
		System.out.println("Notification Property: " + notificationProperty);
		System.out.println("Template Languages: " + templateLanguages);
		System.out.println("Values Map: " + values);

		String notificationtypeconfig = notificationProperty;
		String notificationMobileNo = phoneNumber;
		Set<NotificationType> notificationtype = new HashSet<>();

		if (isNotNullorEmpty(notificationtypeconfig)
				&& !notificationtypeconfig.equalsIgnoreCase(NotificationType.NONE.getName())) {
			if (notificationtypeconfig.contains("|")) {
				String value[] = notificationtypeconfig.split("\\|");
				System.out.println("Multiple Notification Types: " + Arrays.toString(value));
				for (int i = 0; i < 2; i++) {
					String nvalue = value[i];
					System.out.println("Processing Notification Type: " + nvalue);
					processNotification(emailId, notificationMobileNo, notificationtype, nvalue);
				}
			} else {
				System.out.println("Single Notification Type: " + notificationtypeconfig);
				processNotification(emailId, notificationMobileNo, notificationtype, notificationtypeconfig);
			}
		} else {
			System.out.println("Notification type is NONE or empty, skipping notification processing.");
		}

		System.out.println("Resolved Notification Types: " + notificationtype);

		if (notificationtype.contains(NotificationType.SMS)) {
			System.out.println("Invoking SMS Notification");
			invokeSmsNotification(values, sender, notificationMobileNo, templateLanguages);
		}

		if (notificationtype.contains(NotificationType.EMAIL)) {
			System.out.println("Invoking Email Notification");
			invokeEmailNotification(values, emailId, sender, templateLanguages);
		}
	}


	/**
	 * Reads notification type from property and set the notification type
	 * 
	 * @param emailId                - email id of Individual
	 * @param phoneNumber            - Phone Number of Individual
	 * @param notificationtype       - Notification type
	 * @param notificationtypeconfig - Notification type from the configuration
	 */

	private void processNotification(String emailId, String phoneNumber, Set<NotificationType> notificationtype,
									 String notificationtypeconfig) {

		System.out.println("----- Inside processNotification -----");
		System.out.println("Notification Config Value: " + notificationtypeconfig);
		System.out.println("Email ID: " + emailId);
		System.out.println("Phone Number: " + phoneNumber);

		String type = notificationtypeconfig;

		if (type.equalsIgnoreCase(NotificationType.SMS.getName())) {
			System.out.println("Requested Notification Type: SMS");
			if (isNotNullorEmpty(phoneNumber)) {
				notificationtype.add(NotificationType.SMS);
				System.out.println("Added Notification Type: SMS");
			} else {
				System.out.println("Phone number is empty, trying to fallback to EMAIL");
				if (isNotNullorEmpty(emailId)) {
					notificationtype.add(NotificationType.EMAIL);
					System.out.println("Fallback Added Notification Type: EMAIL");
				}
			}
		}

		if (type.equalsIgnoreCase(NotificationType.EMAIL.getName())) {
			System.out.println("Requested Notification Type: EMAIL");
			if (isNotNullorEmpty(emailId)) {
				notificationtype.add(NotificationType.EMAIL);
				System.out.println("Added Notification Type: EMAIL");
			} else {
				System.out.println("Email ID is empty, trying to fallback to SMS");
				if (isNotNullorEmpty(phoneNumber)) {
					notificationtype.add(NotificationType.SMS);
					System.out.println("Fallback Added Notification Type: SMS");
				}
			}
		}

		System.out.println("Current Notification Type Set: " + notificationtype);
	}


	private boolean isNotNullorEmpty(String value) {
		return value != null && !value.isEmpty() && value.trim().length() > 0;
	}

	/**
	 * To apply Templates for Email or SMS Notifications
	 * 
	 * @param values       - content for Template
	 * @param templateName - Template name to fetch
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private String applyTemplate(Map<String, Object> values, String templateName, List<String> templateLanguages)
			throws IdAuthenticationBusinessException {
		try {
			Objects.requireNonNull(templateName);
			return idTemplateManager.applyTemplate(templateName, values, templateLanguages);
		} catch (IOException e) {
			// FIXME change the error code
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Sms notification.
	 *
	 * @param values               the values
	 * @param sender               the sender
	 * @param contentTemplate      the content template
	 * @param notificationMobileNo the notification mobile no
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void invokeSmsNotification(Map<String, Object> values, SenderType sender, String notificationMobileNo, List<String> templateLanguages)
			throws IdAuthenticationBusinessException {

		System.out.println("----- Inside invokeSmsNotification -----");
		System.out.println("Sender: " + sender);
		System.out.println("Mobile Number: " + notificationMobileNo);
		System.out.println("Template Languages: " + templateLanguages);
		System.out.println("Values Map: " + values);

		String authSmsTemplate = EnvUtil.getAuthSmsTemplate();
		String otpSmsTemplate = EnvUtil.getOtpSmsTemplate();
		System.out.println("Auth SMS Template: " + authSmsTemplate);
		System.out.println("OTP SMS Template: " + otpSmsTemplate);

		String contentTemplate = "";
		if (sender == SenderType.AUTH && authSmsTemplate != null) {
			contentTemplate = authSmsTemplate;
			System.out.println("Selected SMS Template: AUTH");
		} else if (sender == SenderType.OTP && otpSmsTemplate != null) {
			contentTemplate = otpSmsTemplate;
			System.out.println("Selected SMS Template: OTP");
		} else {
			System.out.println("No SMS template found for sender: " + sender);
		}

		String smsTemplate = applyTemplate(values, contentTemplate, templateLanguages);
		System.out.println("Final SMS Content: " + smsTemplate);

		notificationManager.sendSmsNotification(notificationMobileNo, smsTemplate);
		System.out.println("SMS sent to: " + notificationMobileNo);
	}


	/**
	 * Email notification.
	 *
	 * @param values          the values
	 * @param emailId         the email id
	 * @param sender          the sender
	 * @param contentTemplate the content template
	 * @param subjectTemplate the subject template
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void invokeEmailNotification(Map<String, Object> values, String emailId, SenderType sender, List<String> templateLanguages)
			throws IdAuthenticationBusinessException {
		String otpContentTemaplate = EnvUtil.getOtpContentTemplate();
		String authEmailSubjectTemplate = EnvUtil.getAuthEmailSubjectTemplate();
		String authEmailContentTemplate = EnvUtil.getAuthEmailContentTemplate();
		String otpSubjectTemplate = EnvUtil.getOtpSubjectTemplate();

		String contentTemplate = "";
		String subjectTemplate = "";
		if (sender == SenderType.AUTH && authEmailSubjectTemplate != null && authEmailContentTemplate != null) {
			subjectTemplate = authEmailSubjectTemplate;
			contentTemplate = authEmailContentTemplate;
		} else if (sender == SenderType.OTP && otpSubjectTemplate != null && otpContentTemaplate != null) {
			subjectTemplate = otpSubjectTemplate;
			contentTemplate = otpContentTemaplate;
		}

		String mailSubject = applyTemplate(values, subjectTemplate, templateLanguages);
		String mailContent = applyTemplate(values, contentTemplate, templateLanguages);
		notificationManager.sendEmailNotification(emailId, mailSubject, mailContent);
	}
	
	/**
	 * This method gets the template languages in following order.
	 * 1. Gets user preferred languages if not
	 * 2. Gets default template languages from configuration if not
	 * 3. Gets the data capture languages
	 * @param idInfo
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private List<String> getTemplateLanguages(Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException {
		List<String> userPreferredLangs = idInfoFetcher.getUserPreferredLanguages(idInfo);
		List<String> defaultTemplateLanguges = userPreferredLangs.isEmpty()
				? idInfoFetcher.getTemplatesDefaultLanguageCodes()
				: userPreferredLangs;
		if (defaultTemplateLanguges.isEmpty()) {
			List<String> dataCaptureLanguages = languageUtil.getDataCapturedLanguages(DemoMatchType.NAME, idInfo);
			Collections.sort(dataCaptureLanguages, languageComparator);
			return dataCaptureLanguages;
		}

		return defaultTemplateLanguges;

	}
}