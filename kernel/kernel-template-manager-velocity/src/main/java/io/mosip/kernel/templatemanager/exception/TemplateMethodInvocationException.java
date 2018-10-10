package io.mosip.kernel.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-1
 */
public class TemplateMethodInvocationException extends BaseUncheckedException{

	private static final long serialVersionUID = 6360842063626691912L;

	public TemplateMethodInvocationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
