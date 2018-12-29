package io.mosip.preregistration.booking.errorcodes;

/**
 * Error codes
 * 
 * @author M1046129
 *@author M1044479
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
	PRG_BOOK_RCI_009("PRG_BOOK_RCI_009"), // Invalid Date and Time
	PRG_BOOK_RCI_010("PRG_BOOK_RCI_010"), // Booking Table not accessible
	PRG_BOOK_RCI_011("PRG_BOOK_RCI_011"), // status code updation failed
	PRG_BOOK_RCI_012("PRG_BOOK_RCI_012"), // get status code failed
	PRG_BOOK_RCI_014("PRG_BOOK_RCI_014"), // no slots available for that date
	PRG_BOOK_RCI_015("PRG_BOOK_RCI_015"), // No time slots are assigned to that registration center
	PRG_BOOK_RCI_013("PRG_BOOK_RCI_013"), // Booking data not found
	PRG_BOOK_RCI_016("PRG_BOOK_RCI_016"),//  availability table is not accessable
	PRG_BOOK_RCI_017("PRG_BOOK_RCI_017"), // Appointment time slot is already canceled.
	PRG_BOOK_RCI_018("PRG_BOOK_RCI_018"), // Appointment can not be canceled.
	PRG_BOOK_RCI_019("PRG_BOOK_RCI_019"), // Cancel Appointmenct  failed
	PRG_BOOK_RCI_020("PRG_BOOK_RCI_020"), // Master service not available
	PRG_BOOK_RCI_021("PRG_BOOK_RCI_021"), //Rebooking failed
	PRG_BOOK_RCI_022("PRG_BOOK_RCI_022"),//Invalid request parameter
	PRG_PAM_APP_002("PRG_PAM_APP_002"), PRG_BOOK_002("PRG_BOOK_002"), PRG_BOOK_001("PRG_BOOK_001");

	
	private ErrorCodes(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
}
