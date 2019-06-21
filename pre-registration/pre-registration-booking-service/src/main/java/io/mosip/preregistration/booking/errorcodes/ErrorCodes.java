
/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.errorcodes;

/**
 * This Enum provides the constant variables to define Error codes.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
public enum ErrorCodes {
	PRG_BOOK_RCI_001("PRG_BOOK_RCI_001"), // Appointment cannot be booked
	PRG_BOOK_RCI_002("PRG_BOOK_RCI_002"), // availability not thr for selected time
	PRG_BOOK_RCI_003("PRG_BOOK_RCI_003"), // User has not been selected any time slot
	PRG_BOOK_RCI_004("PRG_BOOK_RCI_004"), // Appointment time slot is already booked
	PRG_BOOK_RCI_005("PRG_BOOK_RCI_005"), // Appointment booking failed
	PRG_BOOK_RCI_006("PRG_BOOK_RCI_006"), // Pre id not found
	PRG_BOOK_RCI_007("PRG_BOOK_RCI_007"), // reg center id not found
	PRG_BOOK_RCI_008("PRG_BOOK_RCI_008"), // date is not selected
	PRG_BOOK_RCI_010("PRG_BOOK_RCI_010"), // Booking Table not accessible
	PRG_BOOK_RCI_011("PRG_BOOK_RCI_011"), // status code updation failed
	PRG_BOOK_RCI_012("PRG_BOOK_RCI_012"), // get status code failed
	PRG_BOOK_RCI_014("PRG_BOOK_RCI_014"), // no slots available for that date
	PRG_BOOK_RCI_015("PRG_BOOK_RCI_015"), // No time slots are assigned to that registration center
	PRG_BOOK_RCI_013("PRG_BOOK_RCI_013"), // Booking data not found
	PRG_BOOK_RCI_016("PRG_BOOK_RCI_016"), // availability table is not accessable
	PRG_BOOK_RCI_017("PRG_BOOK_RCI_017"), // Appointment time slot is already canceled.
	PRG_BOOK_RCI_018("PRG_BOOK_RCI_018"), // Appointment can not be canceled.
	PRG_BOOK_RCI_019("PRG_BOOK_RCI_019"), // Cancel Appointmenct failed
	PRG_BOOK_RCI_020("PRG_BOOK_RCI_020"), // Master service not available
	PRG_BOOK_RCI_021("PRG_BOOK_RCI_021"), // Rebooking failed
	PRG_BOOK_RCI_022("PRG_BOOK_RCI_022"), // Invalid request parameter
	PRG_BOOK_RCI_023("PRG_BOOK_RCI_023"), // DOCUMENTS_NOT_FOUND_EXCEPTION
	PRG_BOOK_RCI_024("PRG_BOOK_RCI_024"), // AVAILABILITY_UPDATE_FAILED
	PRG_BOOK_RCI_025("PRG_BOOK_RCI_025"), // HTTP_CLIENT_EXCEPTION
	PRG_BOOK_RCI_026("PRG_BOOK_RCI_026"), // Booking status cannot be done altered before.
	PRG_BOOK_RCI_027("PRG_BOOK_RCI_027"), // Delete Booking not allowed
	PRG_BOOK_RCI_028("PRG_BOOK_RCI_028"), // FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD
	PRG_BOOK_RCI_029("PRG_BOOK_RCI_029"), // APPONIMENT_CANNOT_BE_CANCELED
	PRG_BOOK_RCI_030("PRG_BOOK_RCI_030"), 
	PRG_BOOK_RCI_031("PRG_BOOK_RCI_031"), // Invalid Booking Date Time
	PRG_BOOK_RCI_032("PRG_BOOK_RCI_032"),// BOOKING_NOT_FOUND_FOR_THE_DATE_RANGE
	PRG_PAM_APP_005("PRG_PAM_APP_005"), // failed to update in demographic entity
	PRG_BOOK_RCI_033("PRG_BOOK_RCI_033"),// notification failed exception
	PRG_BOOK_RCI_034("PRG_BOOK_RCI_034");
	

	/**
	 * @param code
	 */
	private ErrorCodes(String code) {
		this.code = code;
	}

	/**
	 * Code
	 */
	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}