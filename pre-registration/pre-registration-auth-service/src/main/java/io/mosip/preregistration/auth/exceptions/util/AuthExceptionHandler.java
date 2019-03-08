package io.mosip.preregistration.auth.exceptions.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;

/**
 *This class handles the exception caught while login
 *
 * @author Akshay
 *@since 1.0.0
 */

@RestControllerAdvice
public class AuthExceptionHandler {

	@ExceptionHandler(SendOtpFailedException.class)
	public ResponseEntity<MainResponseDTO<?>> sendOtpException(final SendOtpFailedException e,WebRequest request){
		ExceptionJSONInfoDTO errorDetails = new ExceptionJSONInfoDTO(e.getErrorCode(),
				e.getErrorText());
		MainResponseDTO<?> errorRes = new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		errorList.add(errorDetails);
		
		errorRes.setErrors(errorList);
		errorRes.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		return new ResponseEntity<>(errorRes, HttpStatus.OK);
	}
}
