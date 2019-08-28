/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.code;

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
	DOCUMENT_CONSUMED_TABLE_NOT_ACCESSIBLE("Document consumed table not accessible"),//PRG_PAM_BAT_010
	MASTER_DATA_NOT_FOUND("Master data not found"),//PRG_PAM_BAT_011
	NOTIFICATION_CALL_FAILED("Notification service call failed"),//PRG_PAM_BAT_012
	AVAILABILITY_TABLE_NOT_ACCESSABLE("Availablity table not accessible"),//PRG_PAM_BAT_013
	UNABLE_TO_FETCH_THE_PRE_REGISTRATION("Unable to fetch the pre-registration id"),//PRG_PAM_BAT_014
	RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID("Record not found for date range and reg center id"),//PRG_PAM_BAT_015
	BOOKING_DATA_NOT_FOUND("Booking data not found"),//PRG_PAM_BAT_016
	APPOINTMENT_CANNOT_BE_CANCELED("Appointment cannot be canceled"),//PRG_PAM_BAT_017
	CANCEL_BOOKING_BATCH_CALL_FAILED("Cancel appointment service call failed");//PRG_PAM_BAT_018
	
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
