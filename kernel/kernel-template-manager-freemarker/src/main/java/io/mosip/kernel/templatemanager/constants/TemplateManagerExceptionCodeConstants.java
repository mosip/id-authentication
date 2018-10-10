package io.mosip.kernel.templatemanager.constants;

/**
 * 
 * @author Abhishek Kumar
 * @since 2018-10-3
 * @version 1.0.0
 */
public enum TemplateManagerExceptionCodeConstants {
	TEMPLATE_NOT_FOUND("1111111111", "Template resource Could Not Found."), TEMPLATE_PARSING("333333333",
			"exception occurs during template processing"), TEMPLATE_CONFIGURATION_INVALID_DIR("22222222",
					"Invalid template resource directory"),
	TEMPLATE_WRITER_EXCEPTION("5555555","an exception occurs during writing to the writer");
	/**
	 * This variable holds the error code.
	 */
	private String errorCode;

	/**
	 * This variable holds the error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for TemplateManagerConstants Enum.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	TemplateManagerExceptionCodeConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
