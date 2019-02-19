package io.mosip.registration.processor.manual.verification.exception.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.FileNotPresentException;
import io.mosip.registration.processor.manual.verification.exception.InvalidFieldsException;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationErrorDTO;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationBioDemoJsonSerializer;
import io.vertx.core.json.DecodeException;


/**
 * The Class ManualBioExceptionHandler.
 * @author Rishabh Keshari
 */
public class ManualBioDemoExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ManualBioDemoExceptionHandler.class);

	/** The Constant APPLICATION_VERSION. */
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The Constant MODULE_ID. */
	private static final String MODULE_ID = "mosip.registration.processor.packet.id";

	/** The id. */
	private String id="";
	

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Instantiates a new manual bio demo exception handler.
	 */
	public ManualBioDemoExceptionHandler() {

	}
	
	/**
	 * Instantiates a new manual bio demo exception handler.
	 *
	 * @param id the id
	 */
	public ManualBioDemoExceptionHandler(String id) {
		this.id=id;
	}


	/**
	 * Invalid file name exception handler.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String invalidFileNameExceptionHandler(final InvalidFileNameException e) {
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Packet not found exception handler.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String packetNotFoundExceptionHandler(final PacketNotFoundException e) {
		FileNotPresentException fileNotPresentException = new FileNotPresentException(
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getCode(),
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getMessage());
		return buildPacketReceiverExceptionResponse(fileNotPresentException);
	}

	/**
	 * No record assigned exception handler.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String noRecordAssignedExceptionHandler(NoRecordAssignedException e) {
	return buildPacketReceiverExceptionResponse((Exception)e);
	}
	
	/**
	 * Data exception handler.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String dataExceptionHandler(DecodeException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}

	/**
	 * Invalid update exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String invalidUpdateException(InvalidUpdateException e) {
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Invalid filed exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String invalidFiledException(InvalidFieldsException e) {
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Builds the packet receiver exception response.
	 *
	 * @param ex the ex
	 * @return the string
	 */
	private String buildPacketReceiverExceptionResponse(Exception ex) {

		ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(id);
		}
		if (e instanceof BaseCheckedException)
		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();
			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());
			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ManualVerificationErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ManualVerificationErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		response.setVersion("1.0");
		response.setFile(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(ManualVerificationBioDemoResponseDTO.class, new ManualVerificationBioDemoJsonSerializer()).create();
		return gson.toJson(response);
	}
	
	/**
	 * Handler.
	 *
	 * @param exe the exe
	 * @return the string
	 */
	public String handler(Throwable exe) {
		if(exe instanceof InvalidFieldsException)
			return invalidFiledException((InvalidFieldsException) exe);
		if(exe instanceof InvalidUpdateException)
			return invalidUpdateException((InvalidUpdateException)exe);
		if(exe instanceof NoRecordAssignedException)
			return noRecordAssignedExceptionHandler((NoRecordAssignedException)exe);
		if(exe instanceof PacketNotFoundException)
			return packetNotFoundExceptionHandler((PacketNotFoundException)exe);
		if(exe instanceof InvalidFileNameException)
			return invalidFileNameExceptionHandler((InvalidFileNameException)exe);
		else 
			return dataExceptionHandler((DecodeException) exe);
	}



}