package io.mosip.authentication.common.service.integration;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateOTPFrozenExceptionHelper {

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
}
