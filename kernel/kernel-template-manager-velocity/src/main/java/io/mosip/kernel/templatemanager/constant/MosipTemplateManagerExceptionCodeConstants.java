package io.mosip.kernel.templatemanager.constant;

public enum MosipTemplateManagerExceptionCodeConstants {
    TEMPLATE_NOT_FOUND("1111111111", "Template resource Could Not Found."),
    TEMPLATE_PARSING("333333333", "Template resource of any type  has a syntax or other error."), 
    TEMPLATE_INVALID_REFERENCE("44444444", "Reference method in template could not be invoked.");
    /**
    * This variable holds the error code.
    */
    private String errorCode;

    /**
    * This variable holds the error message.
    */
    private String errorMessage;

    /**
    * Constructor for UINErrorConstants Enum.
    * 
     * @param errorCode
    *            the error code.
    * @param errorMessage
    *            the error message.
    */
    MosipTemplateManagerExceptionCodeConstants(String errorCode, String errorMessage) {
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
