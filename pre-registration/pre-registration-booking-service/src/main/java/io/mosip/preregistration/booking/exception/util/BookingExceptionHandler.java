/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.exception.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.serviceimpl.exception.AppointmentReBookingFailedException;
import io.mosip.preregistration.booking.serviceimpl.exception.AvailabilityTableNotAccessableException;
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
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.MasterDataNotAvailableException;
import io.mosip.preregistration.core.exception.NotificationException;
import io.mosip.preregistration.core.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.core.exception.RestCallException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.util.GenericUtil;

/**
 * Exception Handler for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {
	
	/**  The Environment. */
	@Autowired
	protected  Environment env;
	
	/** The id. */
	@Resource
	protected Map<String, String> id;


	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.booking.exception.id}")
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
		MainResponseDTO<?> errorRes = e.getMainResponseDto();
		errorRes.setId(id.get(e.getOperation()));
		errorRes.setVersion(env.getProperty("version"));
		errorRes.setErrors(e.getExptionList());
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
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
	
	@ExceptionHandler(JsonException.class)
	public ResponseEntity<MainResponseDTO<?>> jsonException(final JsonException e) {
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
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> methodArgumentNotValidException(
			final HttpServletRequest httpServletRequest, final MethodArgumentNotValidException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		fieldErrors.forEach(x -> {
			ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(),
					x.getField() + ": " + x.getDefaultMessage());
			errorResponse.getErrors().add(error);
		});
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> onHttpMessageNotReadable(
			final HttpServletRequest httpServletRequest, final HttpMessageNotReadableException e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_015.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.OK);
	}
	
	


	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultErrorHandler(
			final HttpServletRequest httpServletRequest, Exception e) throws IOException {
		ResponseWrapper<ServiceError> errorResponse = setErrors(httpServletRequest);
		ServiceError error = new ServiceError(io.mosip.preregistration.core.errorcodes.ErrorCodes.PRG_CORE_REQ_016.getCode(), e.getMessage());
		errorResponse.getErrors().add(error);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Autowired
	private ObjectMapper objectMapper;
	
	private ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws IOException {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponsetime(LocalDateTime.now(ZoneId.of("UTC")));
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper) {
			requestBody = new String(((ContentCachingRequestWrapper) httpServletRequest).getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			return responseWrapper;
		}
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode reqNode = objectMapper.readTree(requestBody);
		responseWrapper.setId(reqNode.path("id").asText());
		responseWrapper.setVersion(reqNode.path("version").asText());
		return responseWrapper;
	}
	
	@ExceptionHandler(NotificationException.class)
	public ResponseEntity<MainResponseDTO<?>> authServiceException(final NotificationException e,WebRequest request){
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		e.getValidationErrorList().stream().forEach(serviceError->errorList.add(new ExceptionJSONInfoDTO(serviceError.getErrorCode(),serviceError.getMessage())));
		MainResponseDTO<?> errorRes = e.getMainResposneDTO();
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(GenericUtil.getCurrentResponseTime());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}

}