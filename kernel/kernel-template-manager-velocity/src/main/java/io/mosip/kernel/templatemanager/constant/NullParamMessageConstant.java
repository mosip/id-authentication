package io.mosip.kernel.templatemanager.constant;
/**
 * constants for NullPointerException Messages
 * 
 * @author Abhishek Kumar
 * @since 2018-10-10
 * @version 1.0.0
 */
public enum NullParamMessageConstant {
	WRITER("Writer cannot be null"), 
	TEMPLATE_VALUES("Values cannot be null, it requires process template"), 
	TEMPLATE_INPUT_STREAM("Template cannot be null"), 
	ENCODING_TYPE("Encoding type cannot be null"),
	TEMPATE_NAME("Template name cannot be null");
	
	/**
	 * this variable contains the message 
	 */
	private String message;

	NullParamMessageConstant(String message) {
		this.message = message;
	}

	/**
	 * Getter for getting the message
	 * @return
	 */
	public String getMessage() {
		return message;
	}
}
