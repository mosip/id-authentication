package io.mosip.registration.processor.packet.handler;

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
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorResponseDto;
import io.mosip.registration.processor.packet.service.exception.ApisresourceAccessException;
import io.mosip.registration.processor.packet.service.exception.EncryptorBaseCheckedException;
import io.mosip.registration.processor.packet.service.exception.FileNotAccessibleException;
import io.mosip.registration.processor.packet.service.exception.InvalidKeyNoArgJsonException;

@RestControllerAdvice
public class PacketGeneratorExceptionHandler {
	
	@Autowired
	private Environment env;

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketGeneratorExceptionHandler.class);
	
	@ExceptionHandler(EncryptorBaseCheckedException.class)
        public String badrequest(EncryptorBaseCheckedException e)
        {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_PGS_ENCRYPTOR_EXCEPTION.getCode(),"The exception occured while encrypting");
		return packetGenExceptionResponse(e);
        }	
	@ExceptionHandler(FileNotAccessibleException.class)
		public String badRequest(FileNotAccessibleException e)
		{ 
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getCode(),"The required file is not present");
		  return  packetGenExceptionResponse(e);
		}
	@ExceptionHandler(ApisresourceAccessException.class)
	public String badRequest(ApisresourceAccessException e)
	{
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_PGS_API_RESOURCE_NOT_AVAILABLE.getCode(),"API Resource is not available");	
		return packetGenExceptionResponse(e);
	}
	
	@ExceptionHandler(InvalidKeyNoArgJsonException.class)
	public String badRequest(InvalidKeyNoArgJsonException e)
	{
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),PlatformErrorMessages.RPR_PGS_INVALID_KEY_ILLEGAL_ARGUMENT.getCode(),"The key is not valid or the argument passed is illegal");
		return packetGenExceptionResponse(e);
	}
	
	public String packetGenExceptionResponse(Exception ex) 
	{
		PacketGeneratorResponseDto response=new PacketGeneratorResponseDto();
		//PackerGeneratorFailureDto dto = new PackerGeneratorFailureDto();
		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_PACKET_GENERATOR_SERVICE_ID));
		}
		if (ex instanceof BaseCheckedException)

		{
			List<String> errorCodes = ((BaseCheckedException) ex).getCodes();
			List<String> errorTexts = ((BaseCheckedException) ex).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());

			response.setErrors(errors);
		}
		if (ex instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) ex).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) ex).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setErrors(errors);
		}
		response.setResponsetime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		response.setVersion(env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION));
		response.setResponse(null);
		Gson gson = new GsonBuilder().create();
		return gson.toJson(response);
	}
    
}
