package io.mosip.authentication.common.service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.SmsRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.kernel.core.logger.spi.Logger;
import reactor.core.publisher.Mono;

/**
 * The Class NotificationManager.
 *
 * @author Dinesh Karuppiah.T
 */

@Component
public class NotificationManager {


    /** Rest Helper */
    @Autowired
    private RestHelper restHelper;

    /** Rest Request Factory */
    @Autowired
    private RestRequestFactory restRequestFactory;

    /** Logger to log the actions */
    private static Logger logger = IdaLogger.getLogger(NotificationManager.class);

    /**
     * Send sms notification.
     *
     * @param notificationMobileNo the notification mobile no
     * @param message              the message
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public void sendSmsNotification(String notificationMobileNo, String message)
            throws IdAuthenticationBusinessException {
        try {
            SmsRequestDto smsRequestDto = new SmsRequestDto();
            smsRequestDto.setMessage(message);
            smsRequestDto.setNumber(notificationMobileNo);
            RestRequestDTO restRequestDTO = null;
            restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.SMS_NOTIFICATION_SERVICE,
                    RestRequestFactory.createRequest(smsRequestDto), String.class);
            restHelper.requestSync(restRequestDTO, MediaType.APPLICATION_JSON);
        } catch (IDDataValidationException | RestServiceException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside SMS Notification >>>>>", e.getErrorCode(), e.getErrorText());
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
        }
    }

    /**
     * Send SMS notification asynchronously (reactive).
     *
     * @param notificationMobileNo the notification mobile number
     * @param message the message
     * @return Mono<Void> that completes when SMS is sent
     */
    public Mono<Void> sendSmsNotificationAsync(String notificationMobileNo, String message) {
        try {
            SmsRequestDto smsRequestDto = new SmsRequestDto();
            smsRequestDto.setMessage(message);
            smsRequestDto.setNumber(notificationMobileNo);

            RestRequestDTO restRequestDTO = restRequestFactory.buildRequest(
                    RestServicesConstants.SMS_NOTIFICATION_SERVICE,
                    RestRequestFactory.createRequest(smsRequestDto),
                    String.class);

            return restHelper.requestAsync(restRequestDTO, MediaType.APPLICATION_JSON)
                    .then() // Convert Mono<String> to Mono<Void> since we don't need the response
                    .onErrorMap(e -> {
                        if (e instanceof IDDataValidationException || e instanceof RestServiceException) {
                            String errorCode = (e instanceof IDDataValidationException)
                                    ? ((IDDataValidationException) e).getErrorCode()
                                    : ((RestServiceException) e).getErrorCode();
                            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside SMS Notification >>>>>",
                                    errorCode, e.getMessage());
                            return new IdAuthenticationBusinessException(
                                    IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
                        }
                        return e;
                    });
        } catch (Exception e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside SMS Notification >>>>>",
                    "ERROR", e.getMessage());
            return Mono.error(new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e));
        }
    }

    /**
     * Send email notification.
     *
     * @param emailId     the email id
     * @param mailSubject the mail subject
     * @param mailContent the mail content
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public void sendEmailNotification(String emailId, String mailSubject, String mailContent)
            throws IdAuthenticationBusinessException {
        try {
            RestRequestDTO restRequestDTO = null;
            MultiValueMap<String, String> mailRequestDto = new LinkedMultiValueMap<>();
            mailRequestDto.add("mailContent", mailContent);
            mailRequestDto.add("mailSubject", mailSubject);
            mailRequestDto.add("mailTo", emailId);
            restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.MAIL_NOTIFICATION_SERVICE,
                    mailRequestDto, String.class);
            restHelper.requestSync(restRequestDTO, MediaType.MULTIPART_FORM_DATA);
        } catch (IDDataValidationException | RestServiceException e) {
            // FIXME change error code
            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside Mail Notification >>>>>", e.getErrorCode(), e.getErrorText());
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
        }
    }

    /**
     * Send email notification asynchronously (reactive).
     *
     * @param emailId     the email id
     * @param mailSubject the mail subject
     * @param mailContent the mail content
     * @return Mono<Void> that completes when email is sent
     */
    public Mono<Void> sendEmailNotificationAsync(String emailId, String mailSubject, String mailContent) {
        try {
            MultiValueMap<String, String> mailRequestDto = new LinkedMultiValueMap<>();
            mailRequestDto.add("mailContent", mailContent);
            mailRequestDto.add("mailSubject", mailSubject);
            mailRequestDto.add("mailTo", emailId);

            RestRequestDTO restRequestDTO = restRequestFactory.buildRequest(
                    RestServicesConstants.MAIL_NOTIFICATION_SERVICE,
                    mailRequestDto,
                    String.class);

            return restHelper.requestAsync(restRequestDTO, MediaType.MULTIPART_FORM_DATA)
                    .then() // Convert Mono<String> to Mono<Void> since we don't need the response
                    .onErrorMap(e -> {
                        if (e instanceof IDDataValidationException || e instanceof RestServiceException) {
                            String errorCode = (e instanceof IDDataValidationException)
                                    ? ((IDDataValidationException) e).getErrorCode()
                                    : ((RestServiceException) e).getErrorCode();
                            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside Mail Notification >>>>>",
                                    errorCode, e.getMessage());
                            return new IdAuthenticationBusinessException(
                                    IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
                        }
                        return e;
                    });
        } catch (Exception e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, "Inside Mail Notification >>>>>",
                    "ERROR", e.getMessage());
            return Mono.error(new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e));
        }
    }
}