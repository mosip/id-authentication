package io.mosip.authentication.common.service.integration;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.temporal.ChronoUnit;

public class RequireOtpNotFrozenHelper {

    /** The logger. */
    private static Logger logger = IdaLogger.getLogger(RequireOtpNotFrozenHelper.class);

    /** The otp transaction repo. */
    @Autowired
    private OtpTxnRepository otpRepo;

    /** The otp frozen time minutes. */
    @Value("${mosip.ida.otp.frozen.duration.minutes:30}")
    private int otpFrozenTimeMinutes;

    @Autowired
    private CreateOTPFrozenExceptionHelper createOTPFrozenExceptionHelper;

    /**
     * Require otp not frozen.
     *
     * @param otpEntity the otp entity
     * @throws IdAuthenticationBusinessException the id authentication business exception
     */
    public void requireOtpNotFrozen(OtpTransaction otpEntity, boolean saveEntity) throws IdAuthenticationBusinessException {
        if(otpEntity.getStatusCode().equals(IdAuthCommonConstants.FROZEN)) {
            if(!isAfterFrozenDuration(otpEntity)) {
                throw createOTPFrozenExceptionHelper.createOTPFrozenException();
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
        return DateUtils.getUTCCurrentDateTime().isAfter(otpEntity.getUpdDTimes().plus(otpFrozenTimeMinutes, ChronoUnit.MINUTES));
    }

}
