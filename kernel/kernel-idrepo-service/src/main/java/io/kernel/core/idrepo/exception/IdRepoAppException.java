package io.kernel.core.idrepo.exception;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Manoj SP
 *
 */
public class IdRepoAppException extends BaseCheckedException {

    private static final long serialVersionUID = 6748760277721155095L;
    
    private String id;

    public String getId() {
        return id;
    }

    public IdRepoAppException() {
	super();
    }

    public IdRepoAppException(String errorCode, String errorMessage) {
	super(errorCode, errorMessage);
    }

    public IdRepoAppException(String errorCode, String errorMessage, Throwable rootCause) {
	super(errorCode, errorMessage, rootCause);
    }

    public IdRepoAppException(IdRepoErrorConstants exceptionConstant) {
	this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
    }

    public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause) {
	this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
    }
    
    public IdRepoAppException(IdRepoErrorConstants exceptionConstant, String id) {
 	this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage());
 	this.id = id;
     }

     public IdRepoAppException(IdRepoErrorConstants exceptionConstant, Throwable rootCause, String id) {
 	this(exceptionConstant.getErrorCode(), exceptionConstant.getErrorMessage(), rootCause);
 	this.id = id;
     }

}
