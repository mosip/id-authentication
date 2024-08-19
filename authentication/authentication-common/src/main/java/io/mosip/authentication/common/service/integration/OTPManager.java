package io.mosip.authentication.common.service.integration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.integration.dto.OtpGenerateRequestDto;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.NotificationType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * OTPManager handling with OTP-Generation and OTP-Validation.
 *
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 * @author Loganathan S
 */
@Component
public class OTPManager {

    /** The Constant QUERIED_STATUS_CODES. */
    public static final List<String> QUERIED_STATUS_CODES = List.of(IdAuthCommonConstants.ACTIVE_STATUS, IdAuthCommonConstants.FROZEN);

    /** The Constant OTP_EXPIRED. */
    static final String OTP_EXPIRED = "OTP_EXPIRED";

    /** The Constant USER_BLOCKED. */
    private static final String USER_BLOCKED = "USER_BLOCKED";

    /** The rest helper. */
    @Autowired
    private RestHelper restHelper;

    /** The rest request factory. */
    @Autowired
    private RestRequestFactory restRequestFactory;

    /** The security manager. */
    @Autowired
    private IdAuthSecurityManager securityManager;

    /** The otp transaction repo. */
    @Autowired
    private OtpTxnRepository otpRepo;

    /** The notification service. */
    @Autowired
    private NotificationService notificationService;

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(OTPManager.class);

    @Autowired
    private RequireOtpNotFrozenHelper requireOtpNotFrozenHelper;


    /**
     * Generate OTP with information of {@link MediaType } and OTP generation
     * time-out.
     *
     * @param otpRequestDTO the otp request DTO
     * @param idvid the idvid
     * @param idvidType the idvid type
     * @param valueMap      the value map
     * @param templateLanguages the template languages
     * @return String(otp)
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public boolean sendOtp(OtpRequestDTO otpRequestDTO, String idvid, String idvidType, Map<String, String> valueMap, List<String> templateLanguages)
            throws IdAuthenticationBusinessException {

        String refIdHash = securityManager.hash(idvid);
        Optional<OtpTransaction> otpEntityOpt = otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(refIdHash, QUERIED_STATUS_CODES);

        if(otpEntityOpt.isPresent()) {
            OtpTransaction otpEntity = otpEntityOpt.get();
            requireOtpNotFrozenHelper.requireOtpNotFrozen(otpEntity, false);
        }

        String otp = generateOTP(otpRequestDTO.getIndividualId());
        LocalDateTime otpGenerationTime = DateUtils.getUTCCurrentDateTime();
        String otpHash = IdAuthSecurityManager.digestAsPlainText((otpRequestDTO.getIndividualId()
                + EnvUtil.getKeySplitter() + otpRequestDTO.getTransactionID()
                + EnvUtil.getKeySplitter() + otp).getBytes());

        OtpTransaction otpTxn;
        if (otpEntityOpt.isPresent()
                && (otpTxn = otpEntityOpt.get()).getStatusCode().equals(IdAuthCommonConstants.ACTIVE_STATUS)) {
            otpTxn.setOtpHash(otpHash);
            otpTxn.setUpdBy(securityManager.getUser());
            otpTxn.setUpdDTimes(otpGenerationTime);
            otpTxn.setGeneratedDtimes(otpGenerationTime);
            otpTxn.setValidationRetryCount(0);
            otpTxn.setExpiryDtimes(otpGenerationTime.plusSeconds(EnvUtil.getOtpExpiryTime()));
            otpRepo.save(otpTxn);
        } else {
            OtpTransaction txn = new OtpTransaction();
            txn.setId(UUID.randomUUID().toString());
            txn.setRefId(securityManager.hash(otpRequestDTO.getIndividualId()));
            txn.setOtpHash(otpHash);
            txn.setCrBy(securityManager.getUser());
            txn.setCrDtimes(otpGenerationTime);
            txn.setGeneratedDtimes(otpGenerationTime);
            txn.setExpiryDtimes(otpGenerationTime.plusSeconds(
                    EnvUtil.getOtpExpiryTime()));
            txn.setStatusCode(IdAuthCommonConstants.ACTIVE_STATUS);
            otpRepo.save(txn);
        }

        String notificationProperty = null;
        notificationProperty = otpRequestDTO
                .getOtpChannel().stream().map(channel -> NotificationType.getNotificationTypeForChannel(channel)
                        .stream().map(NotificationType::getName).collect(Collectors.joining()))
                .collect(Collectors.joining("|"));

        notificationService.sendOTPNotification(idvid, idvidType, valueMap, templateLanguages, otp, notificationProperty, otpGenerationTime);

        return true;
    }

    /**
     * Generate OTP.
     *
     * @param uin the uin
     * @return the string
     * @throws IdAuthUncheckedException the id auth unchecked exception
     */
    private String generateOTP(String uin) throws IdAuthUncheckedException {
        try {
            OtpGenerateRequestDto otpGenerateRequestDto = new OtpGenerateRequestDto(uin);
            RequestWrapper<OtpGenerateRequestDto> reqWrapper = new RequestWrapper<>();
            reqWrapper.setRequesttime(DateUtils.getUTCCurrentDateTime());
            reqWrapper.setRequest(otpGenerateRequestDto);
            RestRequestDTO restRequest = restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE,
                    reqWrapper, ResponseWrapper.class);
            ResponseWrapper<Map<String, String>> response = restHelper.requestSync(restRequest);
            if ( response!=null && response.getResponse()!=null && response.getResponse().get("status")!=null && response.getResponse().get("status").equals(USER_BLOCKED)) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                        IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), USER_BLOCKED);
                throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
            }
            if(response == null || response.getResponse() == null) {
                throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
            }
            return response.getResponse().get("otp");

        } catch (IDDataValidationException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
                    e.getMessage());
            throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
        } catch (RestServiceException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    IdAuthenticationErrorConstants.SERVER_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.SERVER_ERROR.getErrorMessage());
            throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
        }
    }

}
