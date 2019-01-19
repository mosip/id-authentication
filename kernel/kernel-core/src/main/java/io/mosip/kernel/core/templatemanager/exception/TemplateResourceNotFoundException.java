package io.mosip.kernel.core.templatemanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * this exception thrown when a resource of any type isn't found by the template
 * manager. <br>
 * When this exception is thrown, a best effort will be made to have useful
 * information in the exception's message. For complete information, consult the
 * runtime log.
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 2018-10-1
 */
public class TemplateResourceNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 3070414901455295210L;

	/**
	 * Constructor for set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public TemplateResourceNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
