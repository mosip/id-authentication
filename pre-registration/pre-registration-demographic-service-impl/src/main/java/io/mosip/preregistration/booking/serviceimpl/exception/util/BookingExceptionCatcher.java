/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.exception.util;

import java.time.DateTimeException;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.serviceimpl.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.serviceimpl.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.serviceimpl.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.serviceimpl.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.serviceimpl.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.serviceimpl.exception.JsonException;
import io.mosip.preregistration.booking.serviceimpl.exception.OperationNotAllowedException;
import io.mosip.preregistration.booking.serviceimpl.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.TimeSpanException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.AppointmentBookException;
import io.mosip.preregistration.core.exception.AppointmentCancelException;
import io.mosip.preregistration.core.exception.AppointmentReBookException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.NotificationException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.RestCallException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This class is used to catch the exceptions that occur while Booking
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
public class BookingExceptionCatcher {

	/**
	 * Method to handle the respective exceptions
	 * 
	 * @param ex
	 *            pass the exception
	 */
	public void handle(Exception ex, MainResponseDTO<?> mainResponseDTO) {
		if (ex instanceof RecordNotFoundException) {
			throw new RecordNotFoundException(((RecordNotFoundException) ex).getErrorCode(),
					((RecordNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof InvalidRequestParameterException) {
			throw new InvalidRequestParameterException(((InvalidRequestParameterException) ex).getErrorCode(),
					((InvalidRequestParameterException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof DateTimeException || ex instanceof java.time.format.DateTimeParseException
				|| ex instanceof InvalidDateTimeFormatException || ex instanceof java.text.ParseException) {
			throw new InvalidDateTimeFormatException(
					io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_019.toString(),
					io.mosip.preregistration.core.errorcodes.ErrorMessages.INVALID_DATE_TIME_FORMAT.getMessage(),
					mainResponseDTO);
		} else if (ex instanceof ParseException) {
			throw new ParseException(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_019.toString(),
					io.mosip.preregistration.core.errorcodes.ErrorMessages.INVALID_DATE_TIME_FORMAT.getMessage());
		} else if (ex instanceof DocumentNotFoundException) {
			throw new DocumentNotFoundException(((DocumentNotFoundException) ex).getErrorCode(),
					((DocumentNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof DataAccessLayerException) {

			throw new TableNotAccessibleException(((DataAccessLayerException) ex).getErrorCode(),
					((DataAccessLayerException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingDataNotFoundException) {
			throw new BookingDataNotFoundException(((BookingDataNotFoundException) ex).getErrorCode(),
					((BookingDataNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentBookingFailedException) {
			throw new AppointmentBookingFailedException(((AppointmentBookingFailedException) ex).getErrorCode(),
					((AppointmentBookingFailedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingTimeSlotAlreadyBooked) {
			throw new BookingTimeSlotAlreadyBooked(((BookingTimeSlotAlreadyBooked) ex).getErrorCode(),
					((BookingTimeSlotAlreadyBooked) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentReBookingFailedException) {
			throw new AppointmentReBookingFailedException(((AppointmentReBookingFailedException) ex).getErrorCode(),
					((AppointmentReBookingFailedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingPreIdNotFoundException) {
			throw new BookingPreIdNotFoundException(((BookingPreIdNotFoundException) ex).getErrorCode(),
					((BookingPreIdNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingRegistrationCenterIdNotFoundException) {
			throw new BookingRegistrationCenterIdNotFoundException(
					((BookingRegistrationCenterIdNotFoundException) ex).getErrorCode(),
					((BookingRegistrationCenterIdNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingTimeSlotNotSeletectedException) {
			throw new BookingTimeSlotNotSeletectedException(((BookingTimeSlotNotSeletectedException) ex).getErrorCode(),
					((BookingTimeSlotNotSeletectedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof BookingDateNotSeletectedException) {
			throw new BookingDateNotSeletectedException(((BookingDateNotSeletectedException) ex).getErrorCode(),
					((BookingDateNotSeletectedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentCannotBeBookedException) {
			throw new AppointmentCannotBeBookedException(((AppointmentCannotBeBookedException) ex).getErrorCode(),
					((AppointmentCannotBeBookedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof CancelAppointmentFailedException) {
			throw new CancelAppointmentFailedException(((CancelAppointmentFailedException) ex).getErrorCode(),
					((CancelAppointmentFailedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AvailablityNotFoundException) {
			throw new AvailablityNotFoundException(((AvailablityNotFoundException) ex).getErrorCode(),
					((AvailablityNotFoundException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentAlreadyCanceledException) {
			throw new AppointmentAlreadyCanceledException(((AppointmentAlreadyCanceledException) ex).getErrorCode(),
					((AppointmentAlreadyCanceledException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentCannotBeCanceledException) {
			throw new AppointmentCannotBeCanceledException(((AppointmentCannotBeCanceledException) ex).getErrorCode(),
					((AppointmentCannotBeCanceledException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof DemographicGetStatusException) {
			throw new DemographicGetStatusException(((DemographicGetStatusException) ex).getErrorCode(),
					((DemographicGetStatusException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentBookException) {
			throw new AppointmentBookException(((AppointmentBookException) ex).getErrorCode(),
					((AppointmentBookException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentCancelException) {
			throw new AppointmentCancelException(((AppointmentCancelException) ex).getErrorCode(),
					((AppointmentCancelException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof AppointmentReBookException) {
			throw new AppointmentReBookException(((AppointmentReBookException) ex).getErrorCode(),
					((AppointmentReBookException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof TimeSpanException) {
			throw new TimeSpanException(((TimeSpanException) ex).getErrorCode(),
					((TimeSpanException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof RecordFailedToDeleteException) {
			throw new RecordFailedToDeleteException(((RecordFailedToDeleteException) ex).getErrorCode(),
					((RecordFailedToDeleteException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof OperationNotAllowedException) {
			throw new OperationNotAllowedException(((OperationNotAllowedException) ex).getErrorCode(),
					((OperationNotAllowedException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof DemographicStatusUpdationException) {
			throw new DemographicStatusUpdationException(((DemographicStatusUpdationException) ex).getErrorCode(),
					((DemographicStatusUpdationException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof DateTimeParseException) {
			throw new InvalidDateTimeFormatException(ErrorCodes.PRG_BOOK_RCI_031.getCode(),
					ErrorMessages.INVALID_BOOKING_DATE_TIME.getMessage(), mainResponseDTO);
		} else if (ex instanceof TableNotAccessibleException) {
			throw new TableNotAccessibleException(((TableNotAccessibleException) ex).getErrorCode(),
					((TableNotAccessibleException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof RestCallException) {
			throw new RestCallException(((RestCallException) ex).getErrorCode(),
					((RestCallException) ex).getErrorText(), mainResponseDTO);
		} else if (ex instanceof NotificationException) {
			throw new NotificationException(((NotificationException) ex).getValidationErrorList(), mainResponseDTO);
		} else if (ex instanceof JsonProcessingException) {
			throw new JsonException(ErrorCodes.PRG_BOOK_RCI_034.getCode(),
					ErrorMessages.JSON_PROCESSING_EXCEPTION.getMessage(), mainResponseDTO);
		}

	}

}