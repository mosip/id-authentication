package io.mosip.kernel.core.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * this exception thrown when a resource of any type
 *  has a syntax or other error which prevents it from being parsed.
 *  <br>
 *  When this resource is thrown, a best effort will be made to have
 *  useful information in the exception's message.  For complete
 *  information, consult the runtime log.
 *  
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-4
 */
public class TemplateParsingException extends BaseUncheckedException{

	private static final long serialVersionUID = 1368132089641129425L;

	/**
	 * Constructor for set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public TemplateParsingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	
	/**
	 * Constructor for setting error code, message and cause
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 * @param rootCause
	 *            the specified cause
	 */
	public TemplateParsingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage,rootCause);
	}

}
