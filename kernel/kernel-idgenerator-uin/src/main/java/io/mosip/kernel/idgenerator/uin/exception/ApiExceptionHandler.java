package io.mosip.kernel.idgenerator.uin.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorErrorCode;

/**
 * Class for handling API exceptions
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	private static final String ERR = "error";

	/**
	 * This method handle MethodArgumentNotValidException.
	 * 
	 * @param e
	 *            the exception
	 * @return the response entity.
	 */
	@ExceptionHandler(UinNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> uinNotFoundHandler(UinNotFoundException e) {

		ArrayList<ErrorBean> errorList = new ArrayList<>();
		ErrorBean error = new ErrorBean(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode(),
				UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorMessage());
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

	}

}