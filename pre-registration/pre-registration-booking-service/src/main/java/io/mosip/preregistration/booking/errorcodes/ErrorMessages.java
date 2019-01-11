/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.errorcodes;

/**
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
public enum ErrorMessages {
	/**
	 * ErrorMessage for PRG_BOOK_RCI_001
	 */
	BOOKING_TABLE_NOT_ACCESSIBLE("BOOKING_TABLE_NOT_ACCESSIBLE"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_002
	 */
	REGISTRATION_TABLE_NOT_ACCESSIBLE("REGISTRATION_TABLE_NOT_ACCESSIBLE"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_003
	 */
	USER_HAS_NOT_SELECTED_TIME_SLOT("USER_HAS_NOT_SELECTED_TIME_SLOT"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_004
	 */
	APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED("APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	PREREGISTRATION_ID_NOT_ENTERED("PREREGISTRATION_ID_NOT_ENTERED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	REGISTRATION_CENTER_ID_NOT_ENTERED("REGISTRATION_CENTER_ID_NOT_ENTERED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	BOOKING_DATE_TIME_NOT_SELECTED("BOOKING_DATE_TIME_NOT_SELECTED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_ID("INVALID_ID"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_VERSION("INVALID_VERSION"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_DATE_TIME_FORMAT("INVALID_DATE_TIME_FORMAT"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANNOT_BE_BOOKED("APPOINTMENT_CANNOT_BE_BOOKED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME("AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_BOOKING_FAILED("APPOINTMENT_BOOKING_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	TABLE_NOT_FOUND_EXCEPTION("TABLE_NOT_FOUND_EXCEPTION"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_STATUS_UPDATION_FAILED("DEMOGRAPHIC_STATUS_UPDATION_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	NO_SLOTS_AVAILABLE_FOR_THAT_DATE("NO_SLOTS_AVAILABLE_FOR_THAT_DATE"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER("NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_GET_STATUS_FAILED("DEMOGRAPHIC_GET_STATUS_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_TABLE_NOT_ACCESSABLE("AVAILABILITY_TABLE_NOT_ACCESSABLE"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	BOOKING_DATA_NOT_FOUND("BOOKING_DATA_NOT_FOUND"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANNOT_BE_CANCELED("APPOINTMENT_CANNOT_BE_CANCELED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED("APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANCEL_FAILED("APPOINTMENT_CANCEL_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_REBOOKING_FAILED("APPOINTMENT_REBOOKING_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	MASTER_DATA_NOT_FOUND("MASTER_DATA_NOT_FOUND"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	HOLIDAY_MASTER_DATA_NOT_FOUND("HOLIDAY_MASTER_DATA_NOT_FOUND"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_REQUEST_PARAMETER("INVALID_REQUEST_PARAMETER"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
    DOCUMENTS_NOT_FOUND_EXCEPTION("DOCUMENTS_NOT_FOUND_EXCEPTION"),
    /**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_SERVICE_CALL_FAILED("DEMOGRAPHIC_SERVICE_CALL_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_UPDATE_FAILED("AVAILABILITY_UPDATE_FAILED"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	HTTP_CLIENT_EXCEPTION("HTTP_CLIENT_EXCEPTION");
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
