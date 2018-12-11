package io.mosip.authentication.service.impl.notification.service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.NotificationType;
import io.mosip.authentication.core.dto.indauth.SenderType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.otpgen.facade.OTPFacadeImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
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

	private static final String DATETIME_PATTERN = "datetime.pattern";
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
	
	/** Property Name for Auth SMS Template */
	private static final String AUTH_SMS_TEMPLATE = "mosip.auth.sms.template";

	/** Property Name for OTP SMS Template */
	private static final String OTP_SMS_TEMPLATE = "mosip.otp.sms.template";
	
	/** The Constant STATUS_SUCCESS. */
	private static final String STATUS_SUCCESS = "y";

	/** Property Name for Auth Email Subject Template */
	private static final String AUTH_EMAIL_SUBJECT_TEMPLATE = "mosip.auth.mail.subject.template";

	/** Property Name for Auth Email Content Template */
	private static final String AUTH_EMAIL_CONTENT_TEMPLATE = "mosip.auth.mail.content.template";

	/** Property Name for OTP Subject Template */
	private static final String OTP_SUBJECT_TEMPLATE = "mosip.otp.mail.subject.template";

	/** Property Name for OTP Content Template */
	private static final String OTP_CONTENT_TEMPLATE = "mosip.otp.mail.content.template";
	
	/** The Constant STATUS. */
	private static final String STATUS = "status";
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";
	
	@Autowired
	private Environment env;
	
	/** The demo auth service. */
	@Autowired
	private IdInfoHelper demoHelper;
	
	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;
	
	@Autowired
	IdRepoService idInfoService;
	
	/** ID Template manager */
	@Autowired
	private IdTemplateManager idTemplateManager;
	
	@Autowired
	private NotificationManager notificationManager;
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);
	
	public void sendAuthNotification(AuthRequestDTO authRequestDTO, String uin, AuthResponseDTO authResponseDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException {

		boolean ismaskRequired = Boolean.parseBoolean(env.getProperty("uin.masking.required"));

		Map<String, Object> values = new HashMap<>();
		values.put(NAME, demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo));
		String resTime = authResponseDTO.getResTime();

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();

		ZonedDateTime dateTimeReq = ZonedDateTime.parse(resTime, isoPattern);
		ZonedDateTime dateTimeConvertedToReqZone = dateTimeReq.withZoneSameInstant(zone);
		String changedDate = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty("notification.date.format")));
		String changedTime = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty("notification.time.format")));

		values.put(DATE, changedDate);
		values.put(TIME, changedTime);
		String maskedUin = "";
		if (ismaskRequired) {
			maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(env.getProperty("uin.masking.charcount")));
		}

		values.put(UIN2, maskedUin);
		values.put(AUTH_TYPE,

				Stream.of(DemoAuthType.values()).filter(authType -> authType.isAuthTypeEnabled(authRequestDTO))
						.map(DemoAuthType::getDisplayName).distinct().collect(Collectors.joining(",")));
		if (authResponseDTO.getStatus().equalsIgnoreCase(STATUS_SUCCESS)) {
			values.put(STATUS, "Passed");
		} else {
			values.put(STATUS, "Failed");
		}

		String phoneNumber = null;
		String email = null;
		phoneNumber = demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo);
		email = demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo);
		String notificationType = null;
		if (isAuth) {
			notificationType = env.getProperty("auth.notification.type");
		} else {
			notificationType = env.getProperty("internal.auth.notification.type");
		}

		sendNotification(values, email, phoneNumber, SenderType.AUTH, notificationType);
	}
	
	
	public void sendOtpNotification(OtpRequestDTO otpRequestDto, String otp, String uin,
			String email, String mobileNumber,Map<String, List<IdentityInfoDTO>> idInfo) {
		
		
		String[] dateAndTime = getDateAndTime(otpRequestDto.getReqTime());
		String date = dateAndTime[0];
		String time = dateAndTime[1];

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		try {
			maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(env.getProperty("uin.masking.charcount")));
			values.put("uin", maskedUin);
			values.put("otp", otp);
			values.put("validTime", env.getProperty("otp.expiring.time"));
			values.put(DATE, date);
			values.put(TIME, time);

			values.put("name", demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo));

			sendNotification(values, email, mobileNumber, SenderType.OTP,
					env.getProperty("otp.notification.type"));
		} catch (BaseCheckedException e) {
			mosipLogger.error(SESSION_ID, "send OTP notification to : ", email, "and " + mobileNumber);
		}
	}
	
	private String[] getDateAndTime(String reqquestTime) {

		String[] dateAndTime = new String[2];

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqquestTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime dateTime3 = ZonedDateTime.now(zone);
		ZonedDateTime dateTime = dateTime3.withZoneSameInstant(zone);
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dateAndTime[0] = date;
		String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		dateAndTime[1] = time;

		return dateAndTime;

	}
	
	/**
	 * Method to Send Notification to the Individual via SMS / E-Mail
	 * 
	 * @param notificationtype - specifies notification type
	 * @param values           - list of values to send notification
	 * @param emailId          - sender E-Mail ID
	 * @param phoneNumber      - sender Phone Number
	 * @param sender           - to specify the sender type
	 * @param notificationProperty 
	 * @throws IdAuthenticationBusinessException
	 */
	
	private void sendNotification(Map<String, Object> values, String emailId, String phoneNumber, SenderType sender, String notificationProperty)
			throws IdAuthenticationBusinessException {
		String contentTemplate = null;
		String subjectTemplate = null;
		String notificationtypeconfig = notificationProperty ;	
		String notificationMobileNo = phoneNumber;
		Set<NotificationType> notificationtype = new HashSet<>();

		if (isNotNullorEmpty(notificationtypeconfig) && !notificationtypeconfig.equalsIgnoreCase(NotificationType.NONE.getName())) {
			if (notificationtypeconfig.contains(",")) {
				String value[] = notificationtypeconfig.split(",");
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
			invokeSmsNotification(values, sender, contentTemplate, notificationMobileNo);

		}
		if (notificationtype.contains(NotificationType.EMAIL)) {

			invokeEmailNotification(values, emailId, sender, contentTemplate, subjectTemplate);

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
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.NOTIFICATION_FAILED, e);
		}
	}
	/**
	 * Sms notification.
	 *
	 * @param values the values
	 * @param sender the sender
	 * @param contentTemplate the content template
	 * @param notificationMobileNo the notification mobile no
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void invokeSmsNotification(Map<String, Object> values, SenderType sender, String contentTemplate,
			String notificationMobileNo) throws IdAuthenticationBusinessException {
		if (sender == SenderType.AUTH) {
			contentTemplate = env.getProperty(AUTH_SMS_TEMPLATE);
		} else if (sender == SenderType.OTP) {
			contentTemplate = env.getProperty(OTP_SMS_TEMPLATE);
		}
 
		String smsTemplate = applyTemplate(values, contentTemplate);
		notificationManager.sendSmsNotification(notificationMobileNo, smsTemplate);
	}
	/**
	 * Email notification.
	 *
	 * @param values the values
	 * @param emailId the email id
	 * @param sender the sender
	 * @param contentTemplate the content template
	 * @param subjectTemplate the subject template
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void invokeEmailNotification(Map<String, Object> values, String emailId, SenderType sender,
			String contentTemplate, String subjectTemplate) throws IdAuthenticationBusinessException {
		if (sender == SenderType.AUTH) {
			subjectTemplate = env.getProperty(AUTH_EMAIL_SUBJECT_TEMPLATE);
			contentTemplate = env.getProperty(AUTH_EMAIL_CONTENT_TEMPLATE);
		} else if (sender == SenderType.OTP) {
			subjectTemplate = env.getProperty(OTP_SUBJECT_TEMPLATE);
			contentTemplate = env.getProperty(OTP_CONTENT_TEMPLATE);
		}

		String mailSubject = applyTemplate(values, subjectTemplate);
		String mailContent = applyTemplate(values, contentTemplate);
		notificationManager.sendEmailNotification(emailId, mailSubject, mailContent);
	}

}
