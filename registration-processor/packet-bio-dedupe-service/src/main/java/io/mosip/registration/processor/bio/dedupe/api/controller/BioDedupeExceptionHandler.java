package io.mosip.registration.processor.bio.dedupe.api.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import io.mosip.registration.processor.bio.dedupe.abis.dto.BioDedupeErrorDTO;
import io.mosip.registration.processor.bio.dedupe.abis.dto.BioDedupeResponseDTO;
import io.mosip.registration.processor.bio.dedupe.exception.ABISAbortException;
import io.mosip.registration.processor.bio.dedupe.exception.ABISInternalError;
import io.mosip.registration.processor.bio.dedupe.exception.BioDedupeAppException;
import io.mosip.registration.processor.bio.dedupe.exception.UnableToServeRequestABISException;
import io.mosip.registration.processor.bio.dedupe.exception.UnexceptedError;
import io.mosip.registration.processor.bio.dedupe.request.validator.BioDedupeReqRespJsonSerializer;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;


@RestControllerAdvice
public class BioDedupeExceptionHandler {

	private static final String BIO_DEDUPE_SERVICE_ID = "mosip.packet.bio.dedupe";
	private static final String BIO_DEDUPE_APPLICATION_VERSION = "1.0";
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";



	private static Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeExceptionHandler.class);

	@ExceptionHandler(ABISAbortException.class)
	public String duplicateentry(ABISAbortException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getCause().toString());
		return buildBioDedupeExceptionResponse((Exception)e);
	}

	@ExceptionHandler(JsonMappingException.class)
	public String badRequest(JsonMappingException ex, WebRequest request){
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION.getMessage());
		BioDedupeAppException reg1=new BioDedupeAppException(PlatformErrorMessages.RPR_BDD_JSON_MAPPING_EXCEPTION, ex);
		return handleRegStatusException(reg1,request);
	}

	@ExceptionHandler(JsonParseException.class)
	public String badRequest(JsonParseException ex, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getCode(),PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION.getMessage());
		BioDedupeAppException reg1=new BioDedupeAppException(PlatformErrorMessages.RPR_BDD_JSON_PARSING_EXCEPTION, ex);
		return handleRegStatusException(reg1, request);
	}

	@ExceptionHandler(ABISInternalError.class)
	public String badRequest(ABISInternalError ex) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode(),"langCode must be of 3 characters");
		return buildBioDedupeExceptionResponse((Exception)ex);
	}


	@ExceptionHandler(UnexceptedError.class)
	public String dataExceptionHandler(final UnexceptedError e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildBioDedupeExceptionResponse((Exception)e);
	}
	@ExceptionHandler(UnableToServeRequestABISException.class)
	public String dataExceptionHandler(final UnableToServeRequestABISException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildBioDedupeExceptionResponse((Exception)e);
	}

	@ExceptionHandler(PacketNotFoundException.class)
	public String packetNotFoundExceptionHandler(final PacketNotFoundException ex, WebRequest request) {
		BioDedupeAppException reg1=new BioDedupeAppException(PlatformErrorMessages.RPR_BDD_FILE_NOT_PRESENT, ex);
		return handleRegStatusException(reg1, request);
	}
	@ExceptionHandler(BioDedupeAppException.class)
	protected String handleRegStatusException(BioDedupeAppException e, WebRequest request) {
		return buildBioDedupeExceptionResponse((Exception)e);

	}

	private String buildBioDedupeExceptionResponse(Exception ex) {

		BioDedupeResponseDTO response = new BioDedupeResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(BIO_DEDUPE_SERVICE_ID);
		}
		if (e instanceof BaseCheckedException)

		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<BioDedupeErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new BioDedupeErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());

			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<BioDedupeErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new BioDedupeErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN));
		response.setVersion(BIO_DEDUPE_APPLICATION_VERSION);
		response.setFile(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(BioDedupeResponseDTO.class, new BioDedupeReqRespJsonSerializer()).create();
		return gson.toJson(response);
	}


}
