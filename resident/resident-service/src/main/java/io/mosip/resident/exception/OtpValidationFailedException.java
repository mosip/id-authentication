package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.resident.constant.ResidentErrorCode;

public class OtpValidationFailedException extends BaseUncheckedException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new otp validation failed exception.
     */
    public OtpValidationFailedException() {
        super(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(), ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
    }

    /**
     * Instantiates a new otp validation failed exception.
     *
     * @param errorMessage
     *            the error message
     */
    public OtpValidationFailedException(String errorMessage) {
        super(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode() + EMPTY_SPACE, errorMessage);
    }

    /**
     * Instantiates a new otp validation failed exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public OtpValidationFailedException(String message, Throwable cause) {
        super(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode() + EMPTY_SPACE, message, cause);
    }
}
