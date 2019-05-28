package io.mosip.registration.processor.quality.checker.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

public class FieldNotPresentException extends BaseCheckedException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public FieldNotPresentException() {
        super();
    }

    /**
     * Instantiates a new reg proc checked exception.
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     */
    public FieldNotPresentException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }


}