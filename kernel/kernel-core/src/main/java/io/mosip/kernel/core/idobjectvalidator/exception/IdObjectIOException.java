package io.mosip.kernel.core.idobjectvalidator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorErrorConstant;

/**
 * Exception class when there is any IO interrupt while reading/converting Id Object String.
 * 
 * @author Manoj SP
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class IdObjectIOException extends BaseCheckedException {

	/**
	 * Generated serialization ID.
	 */
	private static final long serialVersionUID = 795618868850353876L;
	
	/**
	 * Instantiates a new id object IO exception.
	 *
	 * @param errorConstant the error constant
	 */
	public IdObjectIOException(IdObjectValidatorErrorConstant errorConstant) {
		super(errorConstant.getErrorCode(), errorConstant.getMessage());
	}
	
	/**
	 * Constructor for JsonIOException class.
	 * 
	 * @param errorCode    the error code of the exception.
	 * @param errorMessage the error message associated with the exception.
	 */
	public IdObjectIOException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	/**
	 * Instantiates a new id object IO exception.
	 *
	 * @param errorConstant the error constant
	 * @param rootCause the root cause
	 */
	public IdObjectIOException(IdObjectValidatorErrorConstant errorConstant, Throwable rootCause) {
		super(errorConstant.getErrorCode(), errorConstant.getMessage(), rootCause);
	}

	/**
	 * Constructor for JsonIOException class.
	 * 
	 * @param errorCode    the error code of the exception.
	 * @param errorMessage the error message associated with the exception.
	 * @param rootCause    root cause of exception.
	 */
	public IdObjectIOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
