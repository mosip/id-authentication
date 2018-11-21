package io.kernel.core.idrepo.exception;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;

/**
 * @author Manoj SP
 *
 */
public class IdRepoDataValidationException extends IdRepoAppException {

    /**
     * 
     */
    private static final long serialVersionUID = -637919650941847283L;

    public IdRepoDataValidationException() {
	super();
    }

    public IdRepoDataValidationException(String errorCode, String errorMessage) {
	super(errorCode, errorMessage);
    }

    public IdRepoDataValidationException(String errorCode, String errorMessage, Throwable rootCause) {
	super(errorCode, errorMessage, rootCause);
    }

    public IdRepoDataValidationException(IdRepoErrorConstants exceptionConstant) {
	super(exceptionConstant);
    }

    public IdRepoDataValidationException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
	super(exceptionConstant, rootCause);
    }

}
