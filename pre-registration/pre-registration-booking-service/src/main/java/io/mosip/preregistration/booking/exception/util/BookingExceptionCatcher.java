/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.exception.util;

import java.time.DateTimeException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;

/**
 * This class is used to catch the exceptions that occur while uploading the
 * document
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
public class BookingExceptionCatcher {
	public void handle(Exception ex) {
		if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.toString(),
					ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_022.toString(),
					ErrorMessages.INVALID_REQUEST_PARAMETER.toString());
		} else if (ex instanceof DateTimeException) {
			throw new InvalidDateTimeFormatException(ErrorCodes.PRG_BOOK_RCI_009.toString(),
					ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());
		} else if (ex instanceof DocumentNotFoundException) {
			throw new DocumentNotFoundException(((DocumentNotFoundException) ex).getErrorCode(),
					((DocumentNotFoundException) ex).getErrorText(), ex.getCause());
		} else if (ex instanceof DataAccessLayerException) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
		}else if(ex instanceof BookingDataNotFoundException) {
			throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
					ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
		}else if (ex instanceof AppointmentBookingFailedException) {
			throw new AppointmentBookingFailedException(ErrorCodes.PRG_BOOK_RCI_005.toString(),
					ErrorMessages.APPOINTMENT_BOOKING_FAILED.toString());
		} else if (ex instanceof BookingTimeSlotAlreadyBooked) {
			throw new BookingTimeSlotAlreadyBooked(ErrorCodes.PRG_BOOK_RCI_004.toString(),
					ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());
		} else if (ex instanceof AppointmentReBookingFailedException) {
			throw new AppointmentReBookingFailedException(ErrorCodes.PRG_BOOK_RCI_021.toString(),
					ErrorMessages.APPOINTMENT_REBOOKING_FAILED.toString());
		} else if (ex instanceof BookingPreIdNotFoundException) {
			throw new BookingPreIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_006.toString(),
					ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());
		} else if (ex instanceof BookingRegistrationCenterIdNotFoundException) {
			throw new BookingRegistrationCenterIdNotFoundException(ErrorCodes.PRG_BOOK_RCI_007.toString(),
					ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());
		}  else if (ex instanceof BookingTimeSlotNotSeletectedException) {
			throw new BookingTimeSlotNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_003.toString(),
					ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());
		}  else if (ex instanceof BookingDateNotSeletectedException) {
			throw new BookingDateNotSeletectedException(ErrorCodes.PRG_BOOK_RCI_008.toString(),
					ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());
		}

	}

}