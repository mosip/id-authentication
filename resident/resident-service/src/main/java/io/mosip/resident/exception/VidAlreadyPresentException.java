package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import java.io.Serializable;

public class VidAlreadyPresentException extends BaseUncheckedException {

    public VidAlreadyPresentException() {
        super();
    }

    /**
     * Instantiates a new exception.
     *
     * @param errorCode    the error code
     * @param errorMessage the error message
     */
    public VidAlreadyPresentException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
