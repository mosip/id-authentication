package io.mosip.authentication.common.service.impl.notification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
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

    /**
     * The Constant AUTH_TYPE.
     */
    private static final String AUTH_TYPE = "authType";
    /**
     * The Constant NAME.
     */
    private static final String NAME = "name";
    /**
     * The Constant TIME.
     */
    private static final String TIME = "time";
    /**
     * The Constant DATE.
     */
    private static final String DATE = "date";

    @Autowired
    private IdInfoFetcher idInfoFetcher;

    /**
     * ID Template manager
     */
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

    private static Logger logger = IdaLogger.getLogger(NotificationServiceImpl.class);

    public void sendAuthNotification(AuthRequestDTO authRequestDTO, String idvid, AuthResponseDTO authResponseDTO,
                                     Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException {

        Map<String, Object> values = new HashMap<>();
        List<String> templateLanguages = getTemplateLanguages(idInfo);

        for (String lang : templateLanguages) {
            values.put(NAME + "_" + lang, entityInfoUtil.getEntityInfoAsString(DemoMatchType.NAME, lang, idInfo));
        }
        Tuple2<String, String> dateAndTime = getDateAndTime(DateUtils.parseToLocalDateTime(authResponseDTO.getResponseTime()));
        values.put(DATE, dateAndTime.getT1());
        values.put(TIME, dateAndTime.getT2());
        String maskedUin = "";
        String charCount = EnvUtil.getUinMaskingCharCount();
        if (charCount != null && !charCount.isEmpty()) {
            maskedUin = MaskUtil.generateMaskValue(idvid, Integer.parseInt(charCount));
        }
        values.put("idvid", maskedUin);
        String idvidType = authRequestDTO.getIndividualIdType();
        values.put("idvidType", idvidType);

        // TODO add for all auth types
        String authTypeStr = Stream
                .of(Stream.<AuthType>of(DemoAuthType.values()), Stream.<AuthType>of(BioAuthType.values()),
                        Stream.<AuthType>of(PinAuthType.values()), Stream.<AuthType>of(PasswordAuthType.values()),
                        Stream.<AuthType>of(KeyBindedTokenAuthType.values()))
                .flatMap(Function.identity())
                .filter(authType -> authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher))
                .peek(System.out::println)
                .map(authType -> authType.getDisplayName(authRequestDTO, idInfoFetcher)).distinct().collect(Collectors.joining(","));
        values.put(AUTH_TYPE, authTypeStr);
        if (authResponseDTO.getResponse().isAuthStatus()) {
            values.put(IdAuthCommonConstants.STATUS, "Passed");
        } else {
            values.put(IdAuthCommonConstants.STATUS, "Failed");
        }

        String phoneNumber = null;
        String email = null;
        phoneNumber = entityInfoUtil.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
        email = entityInfoUtil.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo);
        String notificationType = null;
        if (isAuth) {
            notificationType = EnvUtil.getNotificationType();
        } else {
            // For internal auth no notification is done
            notificationType = NotificationType.NONE.getName();
        }

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
     * Sends notifications (SMS and/or Email) based on the specified notification configuration.
     * <p>
     * This method processes a notification request by determining the types of notifications (SMS, Email)
     * to send based on the provided {@code notificationProperty}. It supports single or multiple notification
     * types specified in the {@code notificationProperty} string, which may contain pipe-separated values
     * (e.g., "SMS|EMAIL"). The method validates the notification configuration, processes the notification
     * types, and invokes the appropriate notification methods ({@code invokeSmsNotification} or
     * {@code invokeEmailNotification}) based on the resolved notification types. It handles the case where
     * no notifications are configured by checking against {@code NotificationType.NONE}.
     * </p>
     *
     * @param values              A map containing key-value pairs with data required to populate notification
     *                            templates (e.g., user details, OTP, or other dynamic content). Must not be null.
     * @param emailId             The recipient's email address for email notifications. May be null or empty
     *                            if email notification is not required.
     * @param phoneNumber         The recipient's phone number for SMS notifications. May be null or empty
     *                            if SMS notification is not required.
     * @param sender              The type of sender initiating the notification (e.g., SYSTEM, ADMIN), as defined
     *                            by the {@code SenderType} enum. Must not be null.
     * @param notificationProperty A string specifying the notification types to be sent. Supports single type
     *                            (e.g., "SMS", "EMAIL") or multiple types separated by a pipe (e.g., "SMS|EMAIL").
     *                            Compared against {@code NotificationType.NONE.getName()} to skip notifications.
     *                            Must not be null or empty unless intentionally set to "NONE".
     * @param templateLanguages   A list of language codes (e.g., ["en", "fr"]) used to select notification
     *                            templates for multilingual support. Must not be null or empty.
     * @throws IdAuthenticationBusinessException If there is an error during notification processing, such as
     *                                          invalid configuration, failure to send notifications, or issues
     *                                          with template resolution. Wraps underlying exceptions for consistent
     *                                          error handling.
     * @see NotificationType
     * @see SenderType
     * @see IdAuthenticationBusinessException
     */
    public void sendNotification(Map<String, Object> values, String emailId, String phoneNumber, SenderType sender,
                                 String notificationProperty, List<String> templateLanguages)
            throws IdAuthenticationBusinessException {
        Set<NotificationType> notificationType = new HashSet<>();

        if (isNotNullOrEmpty(notificationProperty)
                && !notificationProperty.equalsIgnoreCase(NotificationType.NONE.getName())) {
            if (notificationProperty.contains("|")) {
                String[] value = notificationProperty.split("\\|");
                for (int i = 0; i < 2; i++) {
                    processNotification(emailId, phoneNumber, notificationType, value[i]);
                }
            } else {
                processNotification(emailId, phoneNumber, notificationType, notificationProperty);
            }
        }
        if (notificationType.contains(NotificationType.SMS)) {
            invokeSmsNotification(values, sender, phoneNumber, templateLanguages);
        }
        if (notificationType.contains(NotificationType.EMAIL)) {
            invokeEmailNotification(values, emailId, sender, templateLanguages);
        }
    }

    /**
     * Reads notification type from property and set the notification type
     *
     * @param emailId                - email id of Individual
     * @param phoneNumber            - Phone Number of Individual
     * @param notificationType       - Notification type
     * @param notificationTypeConfig - Notification type from the configuration
     */
    private void processNotification(String emailId, String phoneNumber, Set<NotificationType> notificationType,
                                     String notificationTypeConfig) {
        if (notificationTypeConfig.equalsIgnoreCase(NotificationType.SMS.getName())) {
            if (isNotNullOrEmpty(phoneNumber)) {
                notificationType.add(NotificationType.SMS);
            } else {
                if (isNotNullOrEmpty(emailId)) {
                    notificationType.add(NotificationType.EMAIL);
                }
            }
        }

        if (notificationTypeConfig.equalsIgnoreCase(NotificationType.EMAIL.getName())) {
            if (isNotNullOrEmpty(emailId)) {
                notificationType.add(NotificationType.EMAIL);
            } else {
                if (isNotNullOrEmpty(phoneNumber)) {
                    notificationType.add(NotificationType.SMS);
                }
            }
        }
    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty() && !value.trim().isEmpty();
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
        String authSmsTemplate = EnvUtil.getAuthSmsTemplate();
        String otpSmsTemplate = EnvUtil.getOtpSmsTemplate();
        String contentTemplate = "";
        if (sender == SenderType.AUTH && authSmsTemplate != null) {
            contentTemplate = authSmsTemplate;
        } else if (sender == SenderType.OTP && otpSmsTemplate != null) {
            contentTemplate = otpSmsTemplate;
        }

        String smsTemplate = applyTemplate(values, contentTemplate, templateLanguages);
        notificationManager.sendSmsNotificationAsync(notificationMobileNo, smsTemplate).subscribe();
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
    private void invokeEmailNotification(Map<String, Object> values, String emailId, SenderType sender,
                                         List<String> templateLanguages)
            throws IdAuthenticationBusinessException {

        long start = System.currentTimeMillis();
        logger.info("invokeEmailNotification() started for emailId: {}", emailId);

        long templateFetchStart = System.currentTimeMillis();

        String otpContentTemplate = EnvUtil.getOtpContentTemplate();
        String authEmailSubjectTemplate = EnvUtil.getAuthEmailSubjectTemplate();
        String authEmailContentTemplate = EnvUtil.getAuthEmailContentTemplate();
        String otpSubjectTemplate = EnvUtil.getOtpSubjectTemplate();

        logger.info("Template fetching took {} ms", (System.currentTimeMillis() - templateFetchStart));

        long templateSelectionStart = System.currentTimeMillis();

        String contentTemplate = "";
        String subjectTemplate = "";
        if (sender == SenderType.AUTH && authEmailSubjectTemplate != null && authEmailContentTemplate != null) {
            subjectTemplate = authEmailSubjectTemplate;
            contentTemplate = authEmailContentTemplate;
        } else if (sender == SenderType.OTP && otpSubjectTemplate != null && otpContentTemplate != null) {
            subjectTemplate = otpSubjectTemplate;
            contentTemplate = otpContentTemplate;
        }

        logger.info("Template selection took {} ms", (System.currentTimeMillis() - templateSelectionStart));

        long applyTemplateStart = System.currentTimeMillis();

        String mailSubject = applyTemplate(values, subjectTemplate, templateLanguages);
        String mailContent = applyTemplate(values, contentTemplate, templateLanguages);

        logger.info("applyTemplate() for subject/content took {} ms",
                (System.currentTimeMillis() - applyTemplateStart));

        long sendEmailStart = System.currentTimeMillis();
        
        notificationManager.sendEmailNotificationAsync(emailId, mailSubject, mailContent).subscribe();
        
        logger.info("sendEmailNotification() took {} ms", (System.currentTimeMillis() - sendEmailStart));

        logger.info("invokeEmailNotification() total execution time = {} ms",
                (System.currentTimeMillis() - start));
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