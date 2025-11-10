package io.mosip.authentication.common.service.integration;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

/**
 @author Kamesh Shekhar Prasad
 */

@Component
public class RequireOtpNotFrozenHelper {

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(RequireOtpNotFrozenHelper.class);

    /** The otp transaction repo. */
    @Autowired
    private OtpTxnRepository otpRepo;

    /** The otp frozen time minutes. */
    @Value("${mosip.ida.otp.frozen.duration.minutes:30}")
    private int otpFrozenTimeMinutes;

    /** The number of validation attempts allowed. */
    @Value("${mosip.ida.otp.validation.attempt.count.threshold:5}")
    private int numberOfValidationAttemptsAllowed;

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
     * Require otp not frozen.
     *
     * @param otpEntity the otp entity
     * @throws IdAuthenticationBusinessException the id authentication business exception
     */
    public void requireOtpNotFrozen(OtpTransaction otpEntity, boolean saveEntity) throws IdAuthenticationBusinessException {
        if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.FROZEN)) {
            if(!isAfterFrozenDuration(otpEntity)) {
                throw createOTPFrozenException();
            }
            logger.info("OTP Frozen wait time is over. Allowing further.");
            otpEntity.setStatusCode(IdAuthCommonConstants.UNFROZEN);
            if(saveEntity) {
                otpRepo.save(otpEntity);
            }
        }
    }

    /**
     * Checks if the entity is after frozen duration.
     *
     * @param otpEntity the otp entity
     * @return true, if is after frozen duration
     */
    private boolean isAfterFrozenDuration(OtpTransaction otpEntity) {
        return DateUtils2.getUTCCurrentDateTime().isAfter(otpEntity.getUpdDTimes().plus(otpFrozenTimeMinutes, ChronoUnit.MINUTES));
    }

}
