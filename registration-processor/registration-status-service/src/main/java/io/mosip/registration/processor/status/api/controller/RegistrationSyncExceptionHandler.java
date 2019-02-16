package io.mosip.registration.processor.status.api.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusErrorDTO;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusReqRespJsonSerializer;
import io.mosip.registration.processor.status.sync.response.dto.RegStatusResponseDTO;


@RestControllerAdvice(assignableTypes=RegistrationSyncController.class)
public class RegistrationSyncExceptionHandler {

	private static final String REG_SYNC_SERVICE_ID = "mosip.registration.sync";
	private static final String REG_SYNC_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationSyncExceptionHandler.class);

	@ExceptionHandler(TablenotAccessibleException.class)
	public String duplicateentry(TablenotAccessibleException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return buildRegStatusExceptionResponse((Exception)e);
	}

	@ExceptionHandler(JsonMappingException.class)
	public String badRequest(JsonMappingException ex, WebRequest request){
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getMessage());
		RegStatusAppException reg1=new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION, ex);
		return handleRegStatusException(reg1,request);
	}

	@ExceptionHandler(JsonParseException.class)
	public String badRequest(JsonParseException ex, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getMessage());
		RegStatusAppException reg1=new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION, ex);
		return handleRegStatusException(reg1, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String badRequest(MethodArgumentNotValidException ex) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),"langCode must be of 3 characters");
		return buildRegStatusExceptionResponse((Exception)ex);
	}


	@ExceptionHandler(DataIntegrityViolationException.class)
	public String dataExceptionHandler(final DataIntegrityViolationException e, WebRequest request) {

		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildRegStatusExceptionResponse((Exception)e);
	}


	@ExceptionHandler(RegStatusAppException.class)
	protected String handleRegStatusException(RegStatusAppException e, WebRequest request) {
		return buildRegStatusExceptionResponse((Exception)e);

	}

	private String buildRegStatusExceptionResponse(Exception ex) {

		RegStatusResponseDTO response = new RegStatusResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(REG_SYNC_SERVICE_ID);
		}
		if (e instanceof BaseCheckedException)

		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<RegStatusErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new RegStatusErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());

			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<RegStatusErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new RegStatusErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(REG_SYNC_APPLICATION_VERSION);
		response.setResponse(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(RegStatusResponseDTO.class, new RegStatusReqRespJsonSerializer()).create();
		return gson.toJson(response);
	}


}
