package io.mosip.preregistration.booking.exception;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.preregistration.booking.code.ErrorCodes;
import io.mosip.preregistration.booking.dto.ExceptionJSONInfo;
import io.mosip.preregistration.booking.dto.ResponseDto;

/**
 * Exception Handler
 * 
 * @author M1037717
 *
 */
@RestControllerAdvice
public class BookingExceptionHandler {

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
	

}
