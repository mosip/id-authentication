package io.mosip.kernel.templatemanager.velocity.constant;
/**
 * exception constants for template manager
 * 
 * @author Abhishek Kumar
 * @since 08-10-2018
 * @version 1.0.0
 */
public enum TemplateManagerExceptionCodeConstant {
    TEMPLATE_NOT_FOUND("KER-TEM-004", "Template resource not found"),
    TEMPLATE_PARSING("KER-TEM-003", "Exception occured during template processing"), 
    TEMPLATE_INVALID_REFERENCE("KER-TEM-001", "Reference method in template could not be invoked");
	
    /**
    * This variable holds the error code.
    */
    private String errorCode;

    /**
    * This variable holds the error message.
    */
    private String errorMessage;

    /**
    * Constructor for MosipTemplateManagerExceptionCodeConstants Enum.
    * 
     * @param errorCode
    *            the error code.
    * @param errorMessage
    *            the error message.
    */
    TemplateManagerExceptionCodeConstant(String errorCode, String errorMessage) {
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
