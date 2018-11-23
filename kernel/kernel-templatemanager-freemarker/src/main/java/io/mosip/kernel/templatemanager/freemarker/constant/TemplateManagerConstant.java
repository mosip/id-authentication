package io.mosip.kernel.templatemanager.freemarker.constant;

/**
 * Constants for NullPointerException Messages
 * 
 * @author Abhishek Kumar
 * @since 10-10-2018
 * @version 1.0.0
 */
public enum TemplateManagerConstant {
	WRITER_NULL("Writer cannot be null"), TEMPLATE_VALUES_NULL(
			"Values cannot be null, it requires process template"), TEMPLATE_INPUT_STREAM_NULL(
					"Template cannot be null"), ENCODING_TYPE_NULL(
							"Encoding type cannot be null"), TEMPATE_NAME_NULL("Template name cannot be null");

	/**
	 * this variable contains the message
	 */
	private String message;

	/**
	 * TemplateManagerConstant Enum constructor for setting message
	 * 
	 * @param message
	 */
	TemplateManagerConstant(String message) {
		this.message = message;
	}

	/**
	 * getter for getting message
	 * 
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}
