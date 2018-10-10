package io.mosip.kernel.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * throw TemplateConfigurationException if the Configuring the template Manager
 * fails.
 * 
 * @author Abhishek Kumar
 * @since 2018-10-9
 * @version 1.0.0
 */
public class TemplateConfigurationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6167648722650250191L;

	public TemplateConfigurationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
