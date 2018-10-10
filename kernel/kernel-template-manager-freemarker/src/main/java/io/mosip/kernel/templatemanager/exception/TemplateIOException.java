package io.mosip.kernel.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Runtime exception in a template (as opposed to a parsing-time exception).
 * 
 * @author Abhishek Kumar
 * @since 2018-10-9
 * @version 1.0.0
 */
public class TemplateIOException extends BaseUncheckedException {

	private static final long serialVersionUID = 1051626838113273809L;
	/**
	 * constructor for setting error code and message
	 * @param errorCode
	 * @param errorMessage
	 */
	public TemplateIOException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
