package io.mosip.registration.processor.bio.dedupe.api.controller.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.bio.dedupe.api.controller.BioDedupeController;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusResponseDTO;

@RestControllerAdvice(assignableTypes=BioDedupeController.class)
public class BioDedupeExceptionHandler {

	private static final String BIO_DEDUPE_SERVICE_ID = "mosip.registration.processor.bio.dedupe.id";
	private static final String BIO_DEDUPE_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	@Autowired
	private Environment env;
	
	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeExceptionHandler.class);

	@ExceptionHandler(AccessDeniedException.class)
	public String accessDenied(AccessDeniedException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getMessage());
		return buildBioDedupeExceptionResponse((Exception)e);
	}
	
	@ExceptionHandler(InvalidTokenException.class)
	public String invalidToken(InvalidTokenException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getMessage());
		return buildBioDedupeExceptionResponse((Exception)e);
	}
	private String buildBioDedupeExceptionResponse(Exception ex) {

		RegStatusResponseDTO response = new RegStatusResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(BIO_DEDUPE_SERVICE_ID));
		}
		if (e instanceof BaseCheckedException)

		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());

			response.setErrors(errors);
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}

		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(BIO_DEDUPE_APPLICATION_VERSION));
		response.setResponse(null);
		Gson gson = new GsonBuilder().create();
		return gson.toJson(response);
	}

}
