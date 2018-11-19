package io.mosip.kernel.core.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * TemplateMethodInvocationException when reference method in template could not
 * be invoked.
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-1
 */
public class TemplateMethodInvocationException extends BaseUncheckedException {

	private static final long serialVersionUID = 6360842063626691912L;

	/**
	 * Constructor for set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public TemplateMethodInvocationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
