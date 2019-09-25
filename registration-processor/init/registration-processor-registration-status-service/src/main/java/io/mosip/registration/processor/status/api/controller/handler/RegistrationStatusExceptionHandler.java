package io.mosip.registration.processor.status.api.controller.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.api.controller.RegistrationStatusController;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusResponseDTO;


@RestControllerAdvice(assignableTypes=RegistrationStatusController.class)
public class RegistrationStatusExceptionHandler {

	private static final String REG_STATUS_SERVICE_ID = "mosip.registration.processor.registration.status.id";
	private static final String REG_STATUS_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	@Autowired
	private Environment env;

	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;

	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;

	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationStatusExceptionHandler.class);

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> accessDenied(AccessDeniedException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegStatusExceptionResponse((Exception) e);
	}

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<Object> duplicateentry(TablenotAccessibleException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return buildRegStatusExceptionResponse((Exception)e);
	}

	@ExceptionHandler(JsonMappingException.class)
	public ResponseEntity<Object> badRequest(JsonMappingException ex, WebRequest request){
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getMessage());
		RegStatusAppException reg1=new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION, ex);
		return handleRegStatusException(reg1,request);
	}

	@ExceptionHandler(JsonParseException.class)
	public ResponseEntity<Object> badRequest(JsonParseException ex, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getMessage());
		RegStatusAppException reg1=new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION, ex);
		return handleRegStatusException(reg1, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> badRequest(MethodArgumentNotValidException ex) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),"langCode must be of 3 characters");
		return buildRegStatusExceptionResponse((Exception)ex);
	}


	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {

		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildRegStatusExceptionResponse((Exception)e);
	}


	@ExceptionHandler(RegStatusAppException.class)
	protected ResponseEntity<Object> handleRegStatusException(RegStatusAppException e, WebRequest request) {
		return buildRegStatusExceptionResponse((Exception)e);

	}
	@ExceptionHandler(InvalidTokenException.class)
	protected ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException e, WebRequest request) {
		return buildRegStatusExceptionResponse((Exception)e);

	}


	private ResponseEntity<Object> buildRegStatusExceptionResponse(Exception ex) {

		RegStatusResponseDTO response = new RegStatusResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_STATUS_SERVICE_ID));
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
		response.setVersion(env.getProperty(REG_STATUS_APPLICATION_VERSION));
		response.setResponse(null);
		Gson gson = new GsonBuilder().serializeNulls().create();

		if(isEnabled) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(RESPONSE_SIGNATURE,digitalSignatureUtility.getDigitalSignature(gson.toJson(response)));
			return ResponseEntity.ok().headers(headers).body(gson.toJson(response));
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}


}
