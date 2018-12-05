package io.mosip.preregistration.booking.errorcodes;

/**
 * Error codes
 * 
 * @author M1046129
 *
 */
public enum ErrorCodes {

	PRG_BOOK_RCI_001, // User has not been selected any time slot
	PRG_BOOK_RCI_002, // Appointment time slot is already booked,
	PRG_BOOK_RCI_003, // Pre id not found
	PRG_BOOK_RCI_004, // reg center id not found
	PRG_BOOK_RCI_005, // date time is not selected
	PRG_BOOK_RCI_006, // Invalid Id
	PRG_BOOK_RCI_007, // Invalid Version,
	PRG_BOOK_RCI_008, // Invalid Date and Time
	PRG_BOOK_RCI_009, //Appointment cannot be booked
	PRG_PAM_APP_002, PRG_BOOK_002, PRG_BOOK_001

}
