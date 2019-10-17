
/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.errorcodes;

/**
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
/**
 * @author M1043008
 *
 */
public enum ErrorMessages {
	/**
	 * ErrorMessage for PRG_BOOK_RCI_001
	 */
	BOOKING_TABLE_NOT_ACCESSIBLE("Booking table not found"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_002
	 */
	REGISTRATION_TABLE_NOT_ACCESSIBLE("Registration table to accessible"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_003
	 */
	USER_HAS_NOT_SELECTED_TIME_SLOT("User has not selected time slot"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_004
	 */
	APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED("Appointment time slot is already booked"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	PREREGISTRATION_ID_NOT_ENTERED("Preregistration id not entered"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	REGISTRATION_CENTER_ID_NOT_ENTERED("Registration center id not entered"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	BOOKING_DATE_TIME_NOT_SELECTED("Booking date time not selected"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_ID("Invalid id"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_VERSION("Invalid version"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANNOT_BE_BOOKED("Appointment cannot be booked"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME(
			"Appointment booking/re-booking cannot be done as the requested slot is not available. Please select another slot or try again later"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_BOOKING_FAILED("Appointment booking/re-booking cannot be done"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	TABLE_NOT_FOUND_EXCEPTION("Table not found exception"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_STATUS_UPDATION_FAILED("Demographic status updation failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER("No available slots found for specified registration center"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_GET_STATUS_FAILED("Demographic get status failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_TABLE_NOT_ACCESSABLE("Availablity table not accessible"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	BOOKING_DATA_NOT_FOUND("Booking data not found"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANNOT_BE_CANCELED("Appointment cannot be canceled"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED("Appointment time slot is already canceled"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_CANCEL_FAILED("Appointment cancel failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	APPOINTMENT_REBOOKING_FAILED("Appointment rebooking failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	MASTER_DATA_NOT_FOUND("Master data not found"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	HOLIDAY_MASTER_DATA_NOT_FOUND("Holiday master data not found"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	INVALID_REQUEST_PARAMETER("Invalid request parameter"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DOCUMENTS_NOT_FOUND_EXCEPTION("Documents not found exception"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	DEMOGRAPHIC_SERVICE_CALL_FAILED("Demographic service call failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	AVAILABILITY_UPDATE_FAILED("Availablity update failed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_001
	 */
	HTTP_CLIENT_EXCEPTION("Http client exception"),
	/**
	 * ErrorMessage for PRG_TRL_APP_026
	 */
	BOOKING_CANNOT_BE_DONE("Appointment re-booking cannot be done within"),

	/**
	 * ErrorMessage for PRG_TRL_APP_027
	 */
	DELETE_BOOKING_NOT_ALLOWED("Delete booking not allowed"),
	/**
	 * ErrorMessage for PRG_TRL_APP_028
	 */
	FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD("Failed to delete the pre registration record"),
	/**
	 * ErrorMessage for PRG_TRL_APP_029
	 */
	APPONIMENT_CANNOT_BE_CANCELED("Appointment cannot be canceled"),
	/**
	 * ErrorMessage for PRG_TRL_APP_030
	 */
	CANNOT_GET_DETAILS_FOR("Cannot get details for"),
	/**
	 * ErrorMessage for PRG_BOOK_RCI_032
	 */
	RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID("Record not found for date range and reg center id"),

	/*
	 * ErrorMessage for PRG_BOOK_RCI_031
	 */
	INVALID_BOOKING_DATE_TIME("Invalid Booking Date Time"),

	UNABLE_TO_FETCH_THE_PRE_REGISTRATION("Unable to fetch the pre-registration id"),

	NOTIFICATION_CALL_FAILED("Notification service call failed"),

	JSON_PROCESSING_EXCEPTION("Json processing exception"),

	REG_CENTER_ID_NOT_FOUND("Registration center id not found"),

	CANCEL_BOOKING_CANNOT_BE_DONE("Appointment cancelling cannot be done within");
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
