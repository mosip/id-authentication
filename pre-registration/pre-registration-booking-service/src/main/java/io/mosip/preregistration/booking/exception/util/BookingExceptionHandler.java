/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.exception.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailabilityTableNotAccessableException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDateNotSeletectedException;
import io.mosip.preregistration.booking.exception.BookingPreIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingRegistrationCenterIdNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.BookingTimeSlotNotSeletectedException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.DemographicGetStatusException;
import io.mosip.preregistration.booking.exception.DemographicStatusUpdationException;
import io.mosip.preregistration.booking.exception.DocumentNotFoundException;
import io.mosip.preregistration.booking.exception.InvalidDateTimeFormatException;
import io.mosip.preregistration.booking.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.booking.exception.OperationNotAllowedException;
import io.mosip.preregistration.booking.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.exception.RestCallException;
import io.mosip.preregistration.booking.exception.TimeSpanException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * Exception Handler for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {

	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(DemographicStatusUpdationException.class)
	public ResponseEntity<MainResponseDTO<?>> updateStatusException(final DemographicStatusUpdationException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(AvailabilityTableNotAccessableException.class)
	public ResponseEntity<MainResponseDTO<?>> availabilityTableNotAccessableException(
			final AvailabilityTableNotAccessableException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(DemographicGetStatusException.class)
	public ResponseEntity<MainResponseDTO<?>> getStatusException(final DemographicGetStatusException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(MasterDataNotAvailableException.class)
	public ResponseEntity<MainResponseDTO<?>> masterDataNotAvailableException(final MasterDataNotAvailableException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(RestCallException.class)
	public ResponseEntity<MainResponseDTO<?>> databaseerror(final RestCallException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingTimeSlotNotSeletectedException.class)
	public ResponseEntity<MainResponseDTO<?>> timeSlotNotSelected(final BookingTimeSlotNotSeletectedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(AppointmentCannotBeBookedException.class)
	public ResponseEntity<MainResponseDTO<?>> timeSlotNotSelected(final AppointmentCannotBeBookedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());

	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingPreIdNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> preIdNotFound(final BookingPreIdNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingRegistrationCenterIdNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> regCenterNotFound(final BookingRegistrationCenterIdNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(InvalidDateTimeFormatException.class)
	public ResponseEntity<MainResponseDTO<?>> invalidDateTimeException(final InvalidDateTimeFormatException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingTimeSlotAlreadyBooked.class)
	public ResponseEntity<MainResponseDTO<?>> timeSlotAlreadyBooked(final BookingTimeSlotAlreadyBooked e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingDateNotSeletectedException.class)
	public ResponseEntity<MainResponseDTO<?>> bookingDateNotSelected(final BookingDateNotSeletectedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(InvalidRequestParameterException.class)
	public ResponseEntity<MainResponseDTO<?>> bookingDateNotSelected(final InvalidRequestParameterException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDto());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(BookingDataNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> bookingDataNotFound(final BookingDataNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> recordNotFound(final RecordNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(AppointmentAlreadyCanceledException.class)
	public ResponseEntity<MainResponseDTO<?>> appointmentAlreadyCanceledException(
			final AppointmentAlreadyCanceledException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	@ExceptionHandler(AppointmentCannotBeCanceledException.class)
	public ResponseEntity<MainResponseDTO<?>> appointmentCanNotCanceledException(
			final AppointmentCannotBeCanceledException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */

	@ExceptionHandler(CancelAppointmentFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> appointmentCancelFailedException(final CancelAppointmentFailedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	
	@ExceptionHandler(AppointmentReBookingFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> appointmentReBookingFailedException(
			final AppointmentReBookingFailedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	
	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> documentNotFound(final DocumentNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	
	@ExceptionHandler(AvailablityNotFoundException.class)
	public ResponseEntity<MainResponseDTO<?>> availablityNotFound(final AvailablityNotFoundException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	
	@ExceptionHandler(TableNotAccessibleException.class)

	public ResponseEntity<MainResponseDTO<?>> tablenotAccessible(final TableNotAccessibleException e) {
		return GenericUtil.errorResponse(e, e.getMainResposneDTO());
	}

	/**
	 * @param e
	 * @param request
	 * @return error response
	 */
	
	@ExceptionHandler(AppointmentBookingFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> appointmentBookingFailed(final AppointmentBookingFailedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());
	}

	@ExceptionHandler(TimeSpanException.class)
	public ResponseEntity<MainResponseDTO<?>> timeSpanException(final TimeSpanException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());

	}

	@ExceptionHandler(RecordFailedToDeleteException.class)
	public ResponseEntity<MainResponseDTO<?>> recordFailedToDeleteException(final RecordFailedToDeleteException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());

	}

	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<MainResponseDTO<?>> operationNotAllowedException(final OperationNotAllowedException e) {
		return GenericUtil.errorResponse(e, e.getMainResponseDTO());

	}

	@ExceptionHandler(ParseException.class)
	public ResponseEntity<MainResponseDTO<?>> parseException(final ParseException e) {
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(), e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		errorRes.setErrors(errorList);
		errorRes.setId(idUrl);
		errorRes.setVersion(versionUrl);
		errorRes.setResponsetime(DateUtils.formatDate(new Date(), utcDateTimePattern));

		return new ResponseEntity<>(errorRes, HttpStatus.OK);

	}

}