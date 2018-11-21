package io.kernel.core.idrepo.exception;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;

public class IdRepoUnknownException extends IdRepoAppException {

    private static final long serialVersionUID = 4349577830351654726L;

    public IdRepoUnknownException() {
	super();
    }

    public IdRepoUnknownException(String errorCode, String errorMessage) {
	super(errorCode, errorMessage);
    }

    public IdRepoUnknownException(String errorCode, String errorMessage, Throwable rootCause) {
	super(errorCode, errorMessage, rootCause);
    }

    public IdRepoUnknownException(IdRepoErrorConstants exceptionConstant) {
	super(exceptionConstant);
    }

    public IdRepoUnknownException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
	super(exceptionConstant, rootCause);
    }

}
