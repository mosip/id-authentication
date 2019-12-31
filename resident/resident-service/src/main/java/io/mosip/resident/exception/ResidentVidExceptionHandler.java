package io.mosip.resident.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.controller.ResidentVidController;
import io.mosip.resident.dto.ResponseWrapper;

@RestControllerAdvice(assignableTypes = ResidentVidController.class)
public class ResidentVidExceptionHandler {

	private static final String RESIDENT_VID_ID = "resident.vid.id";
	private static final String RESIDENT_VID_VERSION = "resident.vid.version";

	@Autowired
	private Environment env;

	private static Logger logger = LoggerConfiguration.logConfig(ResidentVidExceptionHandler.class);

	@ExceptionHandler(ResidentServiceCheckedException.class)
	public ResponseEntity<Object> residentCheckedException(ResidentServiceCheckedException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(VidAlreadyPresentException.class)
	public ResponseEntity<Object> vidAlreadyPresent(VidAlreadyPresentException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(VidCreationException.class)
	public ResponseEntity<Object> vidCreationFailed(VidCreationException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(ApisResourceAccessException.class)
	public ResponseEntity<Object> apiNotAccessible(ApisResourceAccessException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(OtpValidationFailedException.class)
	public ResponseEntity<Object> otpValidationFailed(OtpValidationFailedException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<Object> invalidInput(InvalidInputException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(VidRevocationException.class)
	public ResponseEntity<Object> vidRevocationFailed(VidRevocationException e) {
		logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	private ResponseEntity<Object> buildRegStatusExceptionResponse(Exception ex) {

		ResponseWrapper response = new ResponseWrapper();
		Throwable e = ex;

		if (e instanceof BaseCheckedException) {
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ServiceError> errors = errorTexts.parallelStream()
					.map(errMsg -> new ServiceError(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		} else if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ServiceError> errors = errorTexts.parallelStream()
					.map(errMsg -> new ServiceError(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		response.setId(env.getProperty(RESIDENT_VID_ID));
		response.setVersion(env.getProperty(RESIDENT_VID_VERSION));
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString());
		response.setResponse(null);

		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

}
