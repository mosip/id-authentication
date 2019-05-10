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
	
	NO_PRE_REGISTRATION_ID_FOUND_TO_UPDATE_STATUS("No pre registration id found to update status"),//PRG_PAM_BAT_001
	NO_PRE_REGISTRATION_ID_FOUND_TO_MOVE("No pre registration id found to move"),// PRG_PAM_BAT_002
	NO_VALID_PRE_REGISTRATION_ID_FOUND("No pre registration id found"),// PRG_PAM_BAT_003
	DEMOGRAPHIC_TABLE_NOT_ACCESSIBLE("Demographic table not accessible"), //PRG_PAM_BAT_004
	REG_APPOINTMENT_TABLE_NOT_ACCESSIBLE("Reg appointment table not accessible"),//PRG_PAM_BAT_005 
	PROCESSED_PREREG_LIST_TABLE_NOT_ACCESSIBLE("Processed prereg list table not accessible"),//PRG_PAM_BAT_006
	DOCUMENT_TABLE_NOT_ACCESSIBLE("Document table not accessible"), //PRG_PAM_BAT_007
	REG_APPOINTMENT_CONSUMED_TABLE_NOT_ACCESSIBLE("Reg appointment consumed table not accessible"), //PRG_PAM_BAT_008
	DEMOGRAPHIC_CONSUMED_TABLE_NOT_ACCESSIBLE("Demographic consumed table not accessible"),//PRG_PAM_BAT_009
	DOCUMENT_CONSUMED_TABLE_NOT_ACCESSIBLE("Document consumed table not accessible");//PRG_PAM_BAT_010
	
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
