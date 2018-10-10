package io.mosip.kernel.templatemanager.constant;

public enum NullParamMessageConstant {
	WRITER("Writer cannot be null"), 
	TEMPLATE_VALUES("Values cannot be null, it require to merge template"), 
	TEMPLATE_INPUT_STREAM("template connot be null"), 
	ENCODING_TYPE("encoding type required"),
	TEMPATE_NAME("template name cannot be null");
	
	/**
	 * this variable contains the message 
	 */
	private String message;

	NullParamMessageConstant(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
