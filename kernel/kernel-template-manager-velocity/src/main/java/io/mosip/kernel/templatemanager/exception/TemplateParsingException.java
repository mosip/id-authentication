package io.mosip.kernel.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-1
 */
public class TemplateParsingException extends BaseUncheckedException{

	private static final long serialVersionUID = 1368132089641129425L;

	public TemplateParsingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
