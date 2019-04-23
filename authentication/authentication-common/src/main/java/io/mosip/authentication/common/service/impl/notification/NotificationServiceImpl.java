package io.mosip.authentication.common.service.impl.notification;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.dto.MaskUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.LanguageType;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.indauth.dto.SenderType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 * 
 * 
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService {
	
	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";
	/** The Constant NAME. */
	private static final String NAME = "name";
	/** The Constant UIN2. */
	private static final String UIN2 = "uin";
	/** The Constant TIME. */
	private static final String TIME = "time";
	/** The Constant DATE. */
	private static final String DATE = "date";

	/** Property Name for Auth Email Subject Template */
	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";

	@Autowired
	private Environment env;

	/** The demo auth service. */
	@Autowired
	private IdInfoHelper infoHelper;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** ID Template manager */
	@Autowired
	private IdTemplateManager idTemplateManager;

	@Autowired
	private NotificationManager notificationManager;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(NotificationServiceImpl.class);

	public void sendAuthNotification(AuthRequestDTO authRequestDTO, String uin, AuthResponseDTO authResponseDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException {

		boolean ismaskRequired = Boolean.parseBoolean(env.getProperty(IdAuthConfigKeyConstants.UIN_MASKING_REQUIRED));

		Map<String, Object> values = new HashMap<>();
		
		String priLang = idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG);
		String namePri = infoHelper.getEntityInfoAsString(DemoMatchType.NAME, priLang, idInfo);
		values.put(NAME, namePri);
		values.put(NAME + "_" + priLang, namePri);
		String secLang = idInfoFetcher.getLanguageCode(LanguageType.SECONDARY_LANG);
		String nameSec = infoHelper.getEntityInfoAsString(DemoMatchType.NAME, secLang, idInfo);
		values.put(NAME + "_" + secLang, nameSec);

		String resTime = authResponseDTO.getResponseTime();

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getRequestTime());
		ZoneId zone = zonedDateTime2.getZone();

		ZonedDateTime dateTimeReq = ZonedDateTime.parse(resTime);
		ZonedDateTime dateTimeConvertedToReqZone = dateTimeReq.withZoneSameInstant(zone);
		String changedDate = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty(IdAuthConfigKeyConstants.NOTIFICATION_DATE_FORMAT)));
		String changedTime = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty(IdAuthConfigKeyConstants.NOTIFICATION_TIME_FORMAT)));

		values.put(DATE, changedDate);
		values.put(TIME, changedTime);
		String maskedUin = "";
		String charCount = env.getProperty(IdAuthConfigKeyConstants.UIN_MASKING_CHARCOUNT);
		if (ismaskRequired && charCount != null) {
			maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(charCount));
		}
		values.put(UIN2, maskedUin);

		// TODO add for all auth types
		String authTypeStr = Stream
				.of(Stream.<AuthType>of(DemoAuthType.values()), Stream.<AuthType>of(BioAuthType.values()),
						Stream.<AuthType>of(PinAuthType.values()))
				.flatMap(Function.identity())
				.filter(authType -> authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher))
				.map(AuthType::getDisplayName).distinct().collect(Collectors.joining(","));
		values.put(AUTH_TYPE, authTypeStr);
		if (authResponseDTO.getResponse().isAuthStatus()) {
			values.put(STATUS, "Passed");
		} else {
			values.put(STATUS, "Failed");
		}

		String phoneNumber = null;
		String email = null;
		phoneNumber = infoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
		email = infoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo);
		String notificationType = null;
		if (isAuth) {
			notificationType = env.getProperty(IdAuthConfigKeyConstants.MOSIP_NOTIFICATIONTYPE);
		} else {
			// For internal auth no notification is done
			notificationType = NotificationType.NONE.getName();
		}

		sendNotification(values, email, phoneNumber, SenderType.AUTH, notificationType);
	}

	/*
	 * Send Otp Notification
	 * 
	 */
	public void sendOtpNotification(OtpRequestDTO otpRequestDto, String otp, String uin, String email,
			String mobileNumber, Map<String, List<IdentityInfoDTO>> idInfo) {

		Entry<String, String> dateAndTime = getDateAndTime(otpRequestDto.getRequestTime(),
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		String date = dateAndTime.getKey();
		String time = dateAndTime.getValue();

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		try {
			String charCount = env.getProperty(IdAuthConfigKeyConstants.UIN_MASKING_CHARCOUNT);
			if (charCount != null) {
				maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(charCount));
			}
			values.put("uin", maskedUin);
			values.put("otp", otp);
			Integer timeInSeconds = env.getProperty(IdAuthConfigKeyConstants.MOSIP_KERNEL_OTP_EXPIRY_TIME, Integer.class);
			int timeInMinutes = (timeInSeconds % 3600) / 60;
			values.put("validTime", String.valueOf(timeInMinutes));
			values.put(DATE, date);
			values.put(TIME, time);

			String priLang = idInfoFetcher.getLanguageCode(LanguageType.PRIMARY_LANG);
			String namePri = infoHelper.getEntityInfoAsString(DemoMatchType.NAME, priLang, idInfo);
			values.put(NAME, namePri);
			values.put(NAME + "_" + priLang, namePri);
			String secLang = idInfoFetcher.getLanguageCode(LanguageType.SECONDARY_LANG);
			String nameSec = infoHelper.getEntityInfoAsString(DemoMatchType.NAME, secLang, idInfo);
			values.put(NAME + "_" + secLang, nameSec);

			sendNotification(values, email, mobileNumber, SenderType.OTP, env.getProperty(IdAuthConfigKeyConstants.MOSIP_NOTIFICATIONTYPE));
		} catch (BaseCheckedException e) {
			mosipLogger.error(SESSION_ID, "send OTP notification to : ", email, "and " + mobileNumber);
		}
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

	private void sendNotification(Map<String, Object> values, String emailId, String phoneNumber, SenderType sender,
			String notificationProperty) throws IdAuthenticationBusinessException {
		String notificationtypeconfig = notificationProperty;
		String notificationMobileNo = phoneNumber;
		Set<NotificationType> notificationtype = new HashSet<>();

		if (isNotNullorEmpty(notificationtypeconfig)
				&& !notificationtypeconfig.equalsIgnoreCase(NotificationType.NONE.getName())) {
			if (notificationtypeconfig.contains("|")) {
				String value[] = notificationtypeconfig.split("\\|");
				for (int i = 0; i < 2; i++) {
					String nvalue = "";
					nvalue = value[i];
					processNotification(emailId, notificationMobileNo, notificationtype, nvalue);
				}
			} else {
				processNotification(emailId, notificationMobileNo, notificationtype, notificationtypeconfig);
			}

		}

		if (notificationtype.contains(NotificationType.SMS)) {
			invokeSmsNotification(values, sender, notificationMobileNo);

		}
		if (notificationtype.contains(NotificationType.EMAIL)) {

			invokeEmailNotification(values, emailId, sender);

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
		String type = notificationtypeconfig;
		if (type.equalsIgnoreCase(NotificationType.SMS.getName())) {
			if (isNotNullorEmpty(phoneNumber)) {
				notificationtype.add(NotificationType.SMS);
			} else {
				if (isNotNullorEmpty(emailId)) {
					notificationtype.add(NotificationType.EMAIL);
				}
			}
		}

		if (type.equalsIgnoreCase(NotificationType.EMAIL.getName())) {
			if (isNotNullorEmpty(emailId)) {
				notificationtype.add(NotificationType.EMAIL);
			} else {
				if (isNotNullorEmpty(phoneNumber)) {
					notificationtype.add(NotificationType.SMS);
				}
			}
		}
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
	private String applyTemplate(Map<String, Object> values, String templateName)
			throws IdAuthenticationBusinessException {
		try {
			Objects.requireNonNull(templateName);
			return idTemplateManager.applyTemplate(templateName, values);
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
	private void invokeSmsNotification(Map<String, Object> values, SenderType sender, String notificationMobileNo)
			throws IdAuthenticationBusinessException {
		String authSmsTemplate = env.getProperty(IdAuthConfigKeyConstants.AUTH_SMS_TEMPLATE);
		String otpSmsTemplate = env.getProperty(IdAuthConfigKeyConstants.OTP_SMS_TEMPLATE);
		String contentTemplate = "";
		if (sender == SenderType.AUTH && authSmsTemplate != null) {
			contentTemplate = authSmsTemplate;
		} else if (sender == SenderType.OTP && otpSmsTemplate != null) {
			contentTemplate = otpSmsTemplate;
		}

		String smsTemplate = applyTemplate(values, contentTemplate);
		notificationManager.sendSmsNotification(notificationMobileNo, smsTemplate);
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
	private void invokeEmailNotification(Map<String, Object> values, String emailId, SenderType sender)
			throws IdAuthenticationBusinessException {
		String otpContentTemaplate = env.getProperty(IdAuthConfigKeyConstants.OTP_CONTENT_TEMPLATE);
		String authEmailSubjectTemplate = env.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_SUBJECT_TEMPLATE);
		String authEmailContentTemplate = env.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_CONTENT_TEMPLATE);
		String otpSubjectTemplate = env.getProperty(IdAuthConfigKeyConstants.OTP_SUBJECT_TEMPLATE);

		String contentTemplate = "";
		String subjectTemplate = "";
		if (sender == SenderType.AUTH && authEmailSubjectTemplate != null && authEmailContentTemplate != null) {
			subjectTemplate = authEmailSubjectTemplate;
			contentTemplate = authEmailContentTemplate;
		} else if (sender == SenderType.OTP && otpSubjectTemplate != null && otpContentTemaplate != null) {
			subjectTemplate = otpSubjectTemplate;
			contentTemplate = otpContentTemaplate;
		}

		String mailSubject = applyTemplate(values, subjectTemplate);
		String mailContent = applyTemplate(values, contentTemplate);
		notificationManager.sendEmailNotification(emailId, mailSubject, mailContent);
	}

	/**
	 * Gets the date and time.
	 *
	 * @param requestTime the request time
	 * @param pattern     the pattern
	 * @return the date and time
	 */
	private static Entry<String, String> getDateAndTime(String requestTime, String pattern) {

		String[] dateAndTime = new String[2];

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(pattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(requestTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime dateTime3 = ZonedDateTime.now(zone);
		ZonedDateTime dateTime = dateTime3.withZoneSameInstant(zone);
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dateAndTime[0] = date;
		String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		dateAndTime[1] = time;

		return new SimpleEntry<>(date, time);

	}

}
