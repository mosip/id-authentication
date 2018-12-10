package io.mosip.preregistration.booking.errorcodes;

/**
 * Error codes
 * 
 * @author M1046129
 *
 */
public enum ErrorCodes {

	PRG_BOOK_RCI_001, // Appointment cannot be booked
	PRG_BOOK_RCI_002, // availability not thr for selected time
	PRG_BOOK_RCI_003, // User has not been selected any time slot
	PRG_BOOK_RCI_004, // Appointment time slot is already booked
	PRG_BOOK_RCI_005, // Appointment booking failed
	PRG_BOOK_RCI_006, // Pre id not found
	PRG_BOOK_RCI_007, // reg center id not found
	PRG_BOOK_RCI_008, // date is not selected
	PRG_BOOK_RCI_009, // Invalid Date and Time
	PRG_BOOK_RCI_010, // Booking Table not accessible
	PRG_BOOK_RCI_011, // status code updation failed
	PRG_BOOK_RCI_012, // get status code failed
	PRG_BOOK_RCI_014, //no slots available for that date
	PRG_BOOK_RCI_015, //No time slots are assigned to that registration center
	PRG_BOOK_RCI_013, // Booking data not found
	PRG_BOOK_RCI_016,//availability table is not accessable
	PRG_BOOK_RCI_017, //Appointment time slot is already canceled.
	PRG_BOOK_RCI_018, //Appointment can not be canceled.
	PRG_BOOK_RCI_019, //Cancel Appointmenct  failed
	PRG_PAM_APP_002, PRG_BOOK_002, PRG_BOOK_001

}
