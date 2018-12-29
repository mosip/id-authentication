package io.mosip.preregistration.booking.exception.util;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.dto.BookingResponseDto;
import io.mosip.preregistration.booking.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailabilityTableNotAccessableException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {

	@ExceptionHandler(DemographicStatusUpdationException.class)
	public ResponseEntity<BookingResponseDto<?>> updateStatusException(final DemographicStatusUpdationException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_011.toString(),
				ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString());
		BookingResponseDto<?> errorRes = new BookingResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AvailabilityTableNotAccessableException.class)
	public ResponseEntity<BookingResponseDto<?>> availabilityTableNotAccessableException(final AvailabilityTableNotAccessableException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_016.toString(),
				ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		BookingResponseDto<?> errorRes = new BookingResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DemographicGetStatusException.class)
	public ResponseEntity<BookingResponseDto<?>> getStatusException(final DemographicGetStatusException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_012.toString(),
				ErrorMessages.DEMOGRAPHIC_GET_STATUS_FAILED.toString());
		BookingResponseDto<?> errorRes = new BookingResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(MasterDataNotAvailableException.class)
	public ResponseEntity<BookingResponseDto<?>> masterDataNotAvailableException(final MasterDataNotAvailableException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_020.toString(),
				ErrorMessages.MASTER_DATA_NOT_FOUND.toString());
		BookingResponseDto<?> errorRes = new BookingResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<BookingResponseDto<?>> databaseerror(final RestCallException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_002.toString(),
				"HTTP_CLIENT_EXCEPTION");
		BookingResponseDto<?> errorRes = new BookingResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BookingTimeSlotNotSeletectedException.class)
	public ResponseEntity<BookingResponseDto<?>> timeSlotNotSelected(final BookingTimeSlotNotSeletectedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_003.toString(),
				ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(AppointmentCannotBeBookedException.class)
	public ResponseEntity<BookingResponseDto<?>> timeSlotNotSelected(final AppointmentCannotBeBookedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_005.toString(),
				ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(BookingPreIdNotFoundException.class)
	public ResponseEntity<BookingResponseDto<?>> preIdNotFound(final BookingPreIdNotFoundException e, WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_006.toString(),
				ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingRegistrationCenterIdNotFoundException.class)
	public ResponseEntity<BookingResponseDto<?>> regCenterNotFound(final BookingRegistrationCenterIdNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_007.toString(),
				ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidDateTimeFormatException.class)
	public ResponseEntity<BookingResponseDto<?>> invalidDateTimeException(final InvalidDateTimeFormatException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_009.toString(),
				ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingTimeSlotAlreadyBooked.class)
	public ResponseEntity<BookingResponseDto<?>> timeSlotAlreadyBooked(final BookingTimeSlotAlreadyBooked e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_004.toString(),
				ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingDateNotSeletectedException.class)
	public ResponseEntity<BookingResponseDto<?>> bookingDateNotSelected(final BookingDateNotSeletectedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_008.toString(),
				ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<BookingResponseDto<?>> bookingDateNotSelected(final InvalidRequestParameterException e,
			WebRequest request) {
		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		ExceptionJSONInfoDTO err = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		responseDto.setStatus(false);
		responseDto.setErr(err);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BookingDataNotFoundException.class)
	public ResponseEntity<BookingResponseDto<?>> bookingDataNotFound(final BookingDataNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_013.toString(),
				ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<BookingResponseDto<?>> recordNotFound(final RecordNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_015.toString(),
				 ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());

		BookingResponseDto<?> responseDto = new BookingResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}
	

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentAlreadyCanceledException.class)
	public ResponseEntity<BookingResponseDto<?>> appointmentAlreadyCanceledException(final AppointmentAlreadyCanceledException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_017.toString(),
				ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED.toString());

		BookingResponseDto responseDto = new BookingResponseDto();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentCannotBeCanceledException.class)
	public ResponseEntity<BookingResponseDto<?>> appointmentCanNotCanceledException(final AppointmentCannotBeCanceledException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_018.toString(),
				ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.toString());

		BookingResponseDto responseDto = new BookingResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(CancelAppointmentFailedException.class)
	public ResponseEntity<BookingResponseDto<?>> appointmentCancelFailedException(final CancelAppointmentFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_019.toString(),
				ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());

		BookingResponseDto responseDto = new BookingResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentReBookingFailedException.class)
	public ResponseEntity<BookingResponseDto<?>> appointmentReBookingFailedException(final AppointmentReBookingFailedException e,
			WebRequest request) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(ErrorCodes.PRG_BOOK_RCI_021.toString(),
				ErrorMessages.APPOINTMENT_REBOOKING_FAILED.toString());

		BookingResponseDto responseDto = new BookingResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(getCurrentResponseTime());
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}
	
	public String getCurrentResponseTime(){
		return DateUtils.formatDate(new Date(System.currentTimeMillis()),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	}

}
