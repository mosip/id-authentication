package io.mosip.kernel.masterdata.exceptionhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.masterdata.exception.DeviceSpecificationDataFatchException;
import io.mosip.kernel.masterdata.exception.DeviceSpecificationNotFoundException;

/**
 * Rest Controller Advice for Master Data
 * 
 * @author Uday Kumar
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class DeviceSpecificationControllerAdvice {
	private static final String ERR = "error";

	@ExceptionHandler(DeviceSpecificationNotFoundException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> deiceSpecificationNotFoundException(
			final DeviceSpecificationNotFoundException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler(DeviceSpecificationDataFatchException.class)
	public ResponseEntity<Map<String, ArrayList<ErrorBean>>> deviceSpecificationDataFatchException(
			final DeviceSpecificationDataFatchException e) {
		ErrorBean error = new ErrorBean(e.getErrorCode(), e.getErrorText());
		Map<String, ArrayList<ErrorBean>> map = setError(error);
		return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
	}

	private Map<String, ArrayList<ErrorBean>> setError(ErrorBean error) {
		ArrayList<ErrorBean> errorList = new ArrayList<>();
		errorList.add(error);
		Map<String, ArrayList<ErrorBean>> map = new HashMap<>();
		map.put(ERR, errorList);
		return map;
	}

}
