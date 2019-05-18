package io.mosip.registration.processor.abis.handler.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class AbisHandlerException extends BaseUncheckedException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new file not found in destination exception.
     */
    public AbisHandlerException() {
        super();

    }

    /**
     * Instantiates a new file not found in destination exception.
     *
     * @param errorMessage the error message
     */
    public AbisHandlerException(String errorMessage) {
        super(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode(), errorMessage);
    }

    /**
     * Instantiates a new file not found in destination exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public AbisHandlerException(String message, Throwable cause) {
        super(PlatformErrorMessages.RPR_ABIS_INTERNAL_ERROR.getCode() + EMPTY_SPACE, message, cause);

    }
}
