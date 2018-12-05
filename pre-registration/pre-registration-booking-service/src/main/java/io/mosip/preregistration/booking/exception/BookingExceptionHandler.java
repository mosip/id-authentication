package io.mosip.preregistration.booking.exception;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.booking.dto.ExceptionJSONInfo;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<ResponseDto> databaseerror(final TablenotAccessibleException e, WebRequest request) {
		ArrayList<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_001.toString(),
				"TABLE_NOT_ACCESSABLE");
		err.add(errorDetails);
		ResponseDto errorRes = new ResponseDto<>();
		errorRes.setErr(err);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<ResponseDto> databaseerror(final RestCallException e, WebRequest request) {
		ArrayList<ExceptionJSONInfo> err = new ArrayList<>();
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_002.toString(),
				"HTTP_CLIENT_EXCEPTION");
		err.add(errorDetails);
		ResponseDto errorRes = new ResponseDto<>();
		errorRes.setErr(err);
		errorRes.setStatus(false);
		errorRes.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(errorRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(BookingTimeSlotNotSeletectedException.class)
	public ResponseEntity<ResponseDto> timeSlotNotSelected(final BookingTimeSlotNotSeletectedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_001.toString(),
				ErrorMessages.USER_HAS_NOT_SELECTED_ANY_TIME_SLOT.toString());

		ResponseDto responseDto = new ResponseDto();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(AppointmentCannotBeBookedException.class)
	public ResponseEntity<ResponseDto> timeSlotNotSelected(final AppointmentCannotBeBookedException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_009.toString(),
				ErrorMessages.APPOINTMENT_CANNOT_BE_BOOKED.toString());

		ResponseDto responseDto = new ResponseDto();

		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(BookingPreIdNotFoundException.class)
	public ResponseEntity<ResponseDto> preIdNotFound(final BookingPreIdNotFoundException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_003.toString(),
				ErrorMessages.PREREGISTRATION_ID_NOT_ENTERED.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(BookingRegistrationCenterIdNotFoundException.class)
	public ResponseEntity<ResponseDto> regCenterNotFound(final BookingRegistrationCenterIdNotFoundException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_004.toString(),
				ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(IncorrectIDException.class)
	public ResponseEntity<ResponseDto> incorrectIdException(final IncorrectIDException e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_006.toString(),
				ErrorMessages.INVALID_ID.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(IncorrectVersionException.class)
	public ResponseEntity<ResponseDto> incorrectVersionException(final IncorrectVersionException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_007.toString(),
				ErrorMessages.INVALID_VERSION.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(InvalidDateTimeFormatException.class)
	public ResponseEntity<ResponseDto> invalidDateTimeException(final InvalidDateTimeFormatException e,
			WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_008.toString(),
				ErrorMessages.INVALID_DATE_TIME_FORMAT.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(TimeSlotAlreadyBooked.class)
	public ResponseEntity<ResponseDto> timeSlotAlreadyBooked(final TimeSlotAlreadyBooked e, WebRequest request) {
		ExceptionJSONInfo errorDetails = new ExceptionJSONInfo(ErrorCodes.PRG_BOOK_RCI_002.toString(),
				ErrorMessages.APPOINTMENT_TIME_SLOT_IS_ALREADY_BOOKED.toString());

		ResponseDto responseDto = new ResponseDto();
		List<ExceptionJSONInfo> err = new ArrayList<>();
		responseDto.setStatus(false);
		err.add(errorDetails);
		responseDto.setErr(err);
		responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
		return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
	}

}
