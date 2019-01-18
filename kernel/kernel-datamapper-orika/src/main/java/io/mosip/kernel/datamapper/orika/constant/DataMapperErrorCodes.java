package io.mosip.kernel.datamapper.orika.constant;

/**
 * Error code constants for Data Mapper implementation
 * 
 * @author Urvil Joshi
 * @author Neha
 * @since 1.0.0
 */

public enum DataMapperErrorCodes {
	
	MAPPING_ERR("KER-DAT-001", "Mapping cannot be done");
	
	/**
	 * Field for error code
	 */
	private final String errorCode;
	
	/**
	 * Field for error message
	 */
	private final String errorMessage;
	
	/**
	 * Function to set errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The error code
	 *            
	 * @param errorMessage
	 * 			  The error message
	 */
	private DataMapperErrorCodes(final String errorCode, final String errorMessage) {
		this.errorCode =  errorCode;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Function to get errorCode
	 * 
	 * @return The error code
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Function to get errorMessage
	 * 
	 * @return The error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
