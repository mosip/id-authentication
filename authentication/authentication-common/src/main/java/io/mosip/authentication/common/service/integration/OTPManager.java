package io.mosip.authentication.common.service.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.mosip.authentication.common.service.impl.OTPServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.mosip.authentication.common.service.helper.RestHelper;
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

    private static Logger LOGGER = IdaLogger.getLogger(OTPManager.class);


    /** The Constant QUERIED_STATUS_CODES. */
    private static final List<String> QUERIED_STATUS_CODES = List.of(IdAuthCommonConstants.ACTIVE_STATUS, IdAuthCommonConstants.FROZEN);

    /** The Constant OTP_EXPIRED. */
    private static final String OTP_EXPIRED = "OTP_EXPIRED";

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

        final String txnId  = (otpRequestDTO != null && otpRequestDTO.getTransactionID() != null)
                ? otpRequestDTO.getTransactionID() : "NA";
        final String flowId = "OTP-MGR";
        final long tStart   = System.nanoTime();
        long t0             = tStart;

        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] START (channels=%s)", flowId, txnId, otpRequestDTO.getOtpChannel()));

        String refIdHash = securityManager.hash(idvid);
        long tA = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=securityManager.hash -> %s", flowId, txnId, ms(tA - t0)));
        t0 = tA;

        Optional<OtpTransaction> otpEntityOpt = otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(refIdHash, QUERIED_STATUS_CODES);
        long tB = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=otpRepo.findLatestActiveOrFrozen -> %s (present=%s)",
                        flowId, txnId, ms(tB - t0), otpEntityOpt.isPresent()));
        t0 = tB;
        if(otpEntityOpt.isPresent()) {
            OtpTransaction otpEntity = otpEntityOpt.get();
            requireOtpNotFrozenHelper.requireOtpNotFrozen(otpEntity, false);
        }

        long tC = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=requireOtpNotFrozen -> %s", flowId, txnId, ms(tC - t0)));
        t0 = tC;

        String otp = generateOTP(otpRequestDTO.getIndividualId());
        long tD = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=generateOTP(REST) -> %s", flowId, txnId, ms(tD - t0)));
        t0 = tD;

        LocalDateTime otpGenerationTime = DateUtils.getUTCCurrentDateTime();
        String otpHash = IdAuthSecurityManager.digestAsPlainText((otpRequestDTO.getIndividualId()
                + EnvUtil.getKeySplitter() + otpRequestDTO.getTransactionID()
                + EnvUtil.getKeySplitter() + otp).getBytes());

        long tE = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=buildOtpHash -> %s", flowId, txnId, ms(tE - t0)));
        t0 = tE;

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

        long tF = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=otpRepo.save(upsert) -> %s", flowId, txnId, ms(tF - t0)));
        t0 = tF;

        String notificationProperty = null;
        notificationProperty = otpRequestDTO
                .getOtpChannel().stream().map(channel -> NotificationType.getNotificationTypeForChannel(channel)
                        .stream().map(NotificationType::getName).collect(Collectors.joining()))
                .collect(Collectors.joining("|"));

        long tG = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=buildNotificationProperty -> %s", flowId, txnId, ms(tG - t0)));
        t0 = tG;

        notificationService.sendOTPNotification(idvid, idvidType, valueMap, templateLanguages, otp, notificationProperty, otpGenerationTime);

        long tH = System.nanoTime();
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] step=notificationService.sendOTPNotification -> %s",
                        flowId, txnId, ms(tH - t0)));

        long total = System.nanoTime() - tStart;
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "sendOtp",
                String.format("[%s|%s] END success total=%s", flowId, txnId, ms(total)));

        return true;
    }

    private static String ms(long nanos) {
        return String.format("%.3f ms", nanos / 1_000_000.0);
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

            // Log start time of API call
            long startTime = System.nanoTime();
            logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
                    "[OTP-MGR|" + uin + "] Starting API call for OTP generation");

            ResponseWrapper<Map<String, String>> response = restHelper.requestSync(restRequest);

            // Log end time and duration of API call
            long durationMs = (System.nanoTime() - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
            logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
                    "[OTP-MGR|" + uin + "] API call completed in " + durationMs + " ms");

            if (response != null && response.getResponse() != null && response.getResponse().get("status") != null
                    && response.getResponse().get("status").equals(USER_BLOCKED)) {
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                        IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE.getErrorCode(), USER_BLOCKED);
                throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.BLOCKED_OTP_VALIDATE);
            }
            if (response == null || response.getResponse() == null) {
                throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
            }
            return response.getResponse().get("otp");

        } catch (IDDataValidationException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "generateOTP",
                    "Data validation failed: " + e.getMessage());
            throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
        } catch (RestServiceException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                    IdAuthenticationErrorConstants.SERVER_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.SERVER_ERROR.getErrorMessage());
            throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
        }
    }

}
