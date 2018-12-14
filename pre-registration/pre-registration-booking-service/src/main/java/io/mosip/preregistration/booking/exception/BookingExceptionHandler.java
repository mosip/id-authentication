package io.mosip.preregistration.booking.exception;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.booking.dto.ExceptionJSONInfo;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exceptions.InvalidRequestParameterException;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {

	@ExceptionHandler(DemographicStatusUpdationException.class)
	public ResponseEntity<ResponseDto<?>> updateStatusException(final DemographicStatusUpdationException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_011.toString(),
				ErrorMessages.DEMOGRAPHIC_STATUS_UPDATION_FAILED.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AvailabilityTableNotAccessableException.class)
	public ResponseEntity<ResponseDto<?>> availabilityTableNotAccessableException(final AvailabilityTableNotAccessableException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_016.toString(),
				ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DemographicGetStatusException.class)
	public ResponseEntity<ResponseDto<?>> getStatusException(final DemographicGetStatusException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_012.toString(),
				ErrorMessages.DEMOGRAPHIC_GET_STATUS_FAILED.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(MasterDataNotAvailableException.class)
	public ResponseEntity<ResponseDto<?>> masterDataNotAvailableException(final MasterDataNotAvailableException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_020.toString(),
				ErrorMessages.MASTER_DATA_NOT_FOUND.toString());
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<ResponseDto<?>> databaseerror(final RestCallException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_002.toString(),
				"HTTP_CLIENT_EXCEPTION");
		ResponseDto<?> errorRes = new ResponseDto<>();
		errorRes.setErr(errorDetails);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BookingTimeSlotNotSeletectedException.class)
	public ResponseEntity<ResponseDto<?>> timeSlotNotSelected(final BookingTimeSlotNotSeletectedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_003.toString(),
				ErrorMessages.USER_HAS_NOT_SELECTED_TIME_SLOT.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(AppointmentCannotBeBookedException.class)
	public ResponseEntity<ResponseDto<?>> timeSlotNotSelected(final AppointmentCannotBeBookedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_005.toString(),
				ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(BookingPreIdNotFoundException.class)
	public ResponseEntity<ResponseDto<?>> preIdNotFound(final BookingPreIdNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_006.toString(),
				ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingRegistrationCenterIdNotFoundException.class)
	public ResponseEntity<ResponseDto<?>> regCenterNotFound(final BookingRegistrationCenterIdNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_007.toString(),
				ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidDateTimeFormatException.class)
	public ResponseEntity<ResponseDto<?>> invalidDateTimeException(final InvalidDateTimeFormatException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_009.toString(),
				ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingTimeSlotAlreadyBooked.class)
	public ResponseEntity<ResponseDto<?>> timeSlotAlreadyBooked(final BookingTimeSlotAlreadyBooked e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_004.toString(),
				ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BookingDateNotSeletectedException.class)
	public ResponseEntity<ResponseDto<?>> bookingDateNotSelected(final BookingDateNotSeletectedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_008.toString(),
				ErrorMessages.BOOKING_DATE_TIME_NOT_SELECTED.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<ResponseDto<?>> bookingDateNotSelected(final InvalidRequestParameterException e,
			WebRequest request) {
		ResponseDto<?> responseDto = new ResponseDto<>();
		ExceptionJSONInfo err = new ExceptionJSONInfo(e.getErrorCode(), e.getErrorText());
		responseDto.setStatus(false);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BookingDataNotFoundException.class)
	public ResponseEntity<ResponseDto<?>> bookingDataNotFound(final BookingDataNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_013.toString(),
				ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ResponseDto<?>> recordNotFound(final BookingDataNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_015.toString(),
				 ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());

		ResponseDto<?> responseDto = new ResponseDto<>();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
	}
	

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentAlreadyCanceledException.class)
	public ResponseEntity<ResponseDto<?>> AppointmentAlreadyCanceledException(final AppointmentAlreadyCanceledException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_017.toString(),
				ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_CANCELED.toString());

		ResponseDto responseDto = new ResponseDto();

		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentCannotBeCanceledException.class)
	public ResponseEntity<ResponseDto<?>> AppointmentCanNotCanceledException(final AppointmentCannotBeCanceledException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_018.toString(),
				ErrorMessages.APPOINTMENT_CANNOT_BE_CANCELED.toString());

		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(CancelAppointmentFailedException.class)
	public ResponseEntity<ResponseDto<?>> AppointmentCancelFailedException(final CancelAppointmentFailedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_019.toString(),
				ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());

		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@ExceptionHandler(AppointmentReBookingFailedException.class)
	public ResponseEntity<ResponseDto<?>> AppointmentReBookingFailedException(final AppointmentReBookingFailedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_021.toString(),
				ErrorMessages.APPOINTMENT_REBOOKING_FAILED.toString());

		ResponseDto responseDto = new ResponseDto();
		responseDto.setStatus(false);
		responseDto.setErr(errorDetails);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

}
