package io.mosip.authentication.common.service.integration;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

/**
 @author Kamesh Shekhar Prasad
 */

public class ValidateOtpHelper {

    /** The security manager. */
    @Autowired
    private IdAuthSecurityManager securityManager;

    /** The otp transaction repo. */
    @Autowired
    private OtpTxnRepository otpRepo;

    @Autowired
    private RequireOtpNotFrozenHelper requireOtpNotFrozen;

    /** The number of validation attempts allowed. */
    @Value("${mosip.ida.otp.validation.attempt.count.threshold:5}")
    private int numberOfValidationAttemptsAllowed;

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(ValidateOtpHelper.class);

    /** The otp frozen time minutes. */
    @Value("${mosip.ida.otp.frozen.duration.minutes:30}")
    private int otpFrozenTimeMinutes;

    /** The Constant OTP_EXPIRED. */
    private static final String OTP_EXPIRED = "OTP_EXPIRED";

    /** The Constant QUERIED_STATUS_CODES. */
    private static final List<String> QUERIED_STATUS_CODES = List.of(IdAuthCommonConstants.ACTIVE_STATUS, IdAuthCommonConstants.FROZEN);


    /**
     * Creates the OTP frozen exception.
     *
     * @return the id authentication business exception
     */
    public IdAuthenticationBusinessException createOTPFrozenException() {
        return new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorCode(),
                String.format(IdAuthenticationErrorConstants.OTP_FROZEN.getErrorMessage(),
                        otpFrozenTimeMinutes + " seconds", numberOfValidationAttemptsAllowed));
    }

    /**
     * Validate method for OTP Validation.
     *
     * @param pinValue the pin value
     * @param otpKey   the otp key
     * @param individualId the individual id
     * @return true, if successful
     * @throws IdAuthenticationBusinessException the id authentication business
     *                                           exception
     */
    public boolean validateOtp(String pinValue, String otpKey, String individualId) throws IdAuthenticationBusinessException {
        String refIdHash = securityManager.hash(individualId);
        Optional<OtpTransaction> otpEntityOpt = otpRepo.findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(refIdHash, QUERIED_STATUS_CODES);
        System.out.println("validateOtp"+"OTP validation started for individualId: {}"+individualId);
        if (otpEntityOpt.isEmpty()) {
            System.out.println("otpEntityOpt is empty for individualId: {}"+ individualId);
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED);
        }

        OtpTransaction otpEntity = otpEntityOpt.get();
        System.out.println("otpEntity is present for individualId: {}"+ individualId);
        requireOtpNotFrozen.requireOtpNotFrozen(otpEntity, true);
        
        if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.UNFROZEN)) {
            System.out.println("OTP is unfrozen. Proceeding with validation.");
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_REQUIRED);
        }

        // At this point it should be active status alone.
        // Increment the validation attempt count.
        int attemptCount = otpEntity.getValidationRetryCount() == null ? 1 : otpEntity.getValidationRetryCount() + 1;
        System.out.println("Incremented validation attempt count to: {}"+ attemptCount);

        String otpHash = getOtpHash(pinValue, otpKey);
        System.out.println("Generated OTP hash for validation: {}" + otpHash);
        if (otpEntity.getOtpHash().equals(otpHash)) {
            System.out.println("OTP hash matches for individualId: {}"+ individualId);
            otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
            otpEntity.setStatusCode(IdAuthCommonConstants.USED_STATUS);
            otpRepo.save(otpEntity);
            if (!otpEntity.getExpiryDtimes().isAfter(DateUtils.getUTCCurrentDateTime())) {
                System.out.println("OTP has expired for individualId: {}"+ individualId);
                logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
                        IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(), OTP_EXPIRED);
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.EXPIRED_OTP);
            }
            return true;
        } else {
            //Set the incremented validation attempt count
            System.out.println("OTP hash does not match for individualId: {}"+ individualId);
            otpEntity.setValidationRetryCount(attemptCount);
            System.out.println("Validation attempt count for individualId {} is now: {}"+ individualId+ attemptCount);
            if (attemptCount >= numberOfValidationAttemptsAllowed) {
                System.out.println("Validation attempt count exceeded for individualId: {}"+ individualId);
                otpEntity.setStatusCode(IdAuthCommonConstants.FROZEN);
                otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
                otpRepo.save(otpEntity);
                throw createOTPFrozenException();
            }
            otpEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
            otpRepo.save(otpEntity);
            System.out.println("OTP validation failed for individualId: {}. Attempt count: {}"+ individualId+ attemptCount);
            return false;
        }
    }

    /**
     * Gets the otp hash.
     *
     * @param pinValue the pin value
     * @param otpKey the otp key
     * @return the otp hash
     */
    private String getOtpHash(String pinValue, String otpKey) {
        return IdAuthSecurityManager.digestAsPlainText(
                (otpKey + EnvUtil.getKeySplitter() + pinValue).getBytes());
    }
}
