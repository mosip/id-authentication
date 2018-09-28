package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mosip.kernel.otpmanagerservice.constants.OtpErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central class for handling exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@RestControllerAdvice
public class OtpControllerAdvice {
	/**
	 * This variable represents the errors.
	 */
	String err = "errors";

	/**
	 * This method handles MethodArgumentNotValidException type of exceptions.
	 * 
	 * @param exception
	 *            The exception
	 * @return The response entity.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, ArrayList<MosipErrors>>> otpGeneratorValidity(
			final MethodArgumentNotValidException exception) {
		MosipErrors error = new MosipErrors(OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_GEN_ILLEGAL_KEY_INPUT.getErrorMessage());
		ArrayList<MosipErrors> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<MosipErrors>> map = new HashMap<>();
		map.put(err, errorList);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * This method handles MosipOtpInvalidArgumentExceptionHandler type of
	 * exceptions.
	 * 
	 * @param exception
	 *            The exception.
	 * @return The response entity.
	 */
	@ExceptionHandler(MosipOtpInvalidArgumentExceptionHandler.class)
	public ResponseEntity<Object> otpValidationValidity(final MosipOtpInvalidArgumentExceptionHandler exception) {
		Map<String, List<MosipErrors>> map = new HashMap<>();
		map.put(err, exception.getList());
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}
}
