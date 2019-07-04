package io.mosip.admin.navigation.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Throw this exception when error occurs in accessing Admin service.
 * 
 * 
 * @author Bal Vikash Sharma
 *
 */
public class AdminServiceException extends BaseUncheckedException {

    private static final long serialVersionUID = -7670752606241791666L;

    public AdminServiceException() {
	super();

    }

    public AdminServiceException(String errorCode, String errorMessage,
	    Throwable rootCause) {
	super(errorCode, errorMessage, rootCause);

    }

    public AdminServiceException(String errorCode, String errorMessage) {
	super(errorCode, errorMessage);

    }

    public AdminServiceException(String errorMessage) {
	super(errorMessage);

    }

}
