/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjobservices.code;

/**
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public enum ErrorMessages {
	
	NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS("NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_CONSUMED_STATUS"),
	NO_PRE_REGISTRATION_ID_FOUND_TO_MOVE("NO_PRE_REGISTRATION_ID_FOUND_TO_MOVE"),
	NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS("NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_EXPIRED_STATUS"),
	DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE("DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE"),
	REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE("REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE"), 
	Processed_Prereg_List_TABLE_NOT_ACCESSIBLE("Processed_Prereg_List_TABLE_NOT_ACCESSIBLE");
	
	/**
	 * @param code
	 */
	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	

}
