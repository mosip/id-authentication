package io.mosip.registration.processor.packet.receiver.exception.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.receiver.dto.ErrorDTO;
import io.mosip.registration.processor.packet.receiver.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.ValidationException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.TimeoutException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.packet.receiver.request.response.serializer.PacketReceiverReqRespJsonSerializer;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;


/**
 * The Class PacketReceiverExceptionHandler.
 * @author Rishabh Keshari
 */
public class PacketReceiverExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketReceiverExceptionHandler.class);

	/** The Constant APPLICATION_VERSION. */
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	private static final String MODULE_ID = "mosip.registration.processor.packet.id";


	/**
	 * Duplicateentry.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String duplicateentry(final DuplicateUploadRequestException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Handle packet not available exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handlePacketNotAvailableException(
			final MissingServletRequestPartException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getMessage(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Handle packet not valid exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handlePacketNotValidException(final PacketNotValidException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}

	/**
	 * Handle file size exceed exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handleFileSizeExceedException(final FileSizeExceedException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}



	/**
	 * Handle tablenot accessible exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handleTablenotAccessibleException(final TablenotAccessibleException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}

	/**
	 * Handle timeout exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handleTimeoutException(final TimeoutException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Handle unexpected exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handleUnexpectedException(final UnexpectedException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Handle validation exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handleValidationException(final ValidationException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(),  e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Data exception handler.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String dataExceptionHandler(final DataIntegrityViolationException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),"RPR-DBE-001 Data integrity violation exception",e.getMessage());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}


	/**
	 * Handle packet not sync exception.
	 *
	 * @param e the e
	 * @return the string
	 */
	public String handlePacketNotSyncException(final PacketNotSyncException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getStackTrace()[0].toString());
		return buildPacketReceiverExceptionResponse((Exception)e);
	}

	/**
	 * Builds the packet receiver exception response.
	 *
	 * @param ex the ex
	 * @return the string
	 */
	private String buildPacketReceiverExceptionResponse(Exception ex) {

		PacketReceiverResponseDTO response = new PacketReceiverResponseDTO();
		Throwable e = ex;
		if (Objects.isNull(response.getId())) {
			response.setId("mosip.registration.packet");
		}
		if (e instanceof BaseCheckedException)

		{
			List<String> errorCodes = ((BaseCheckedException) e).getCodes();
			List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream().map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct().collect(Collectors.toList());

			response.setError(errors.get(0));
		}
		if (e instanceof BaseUncheckedException) {
			List<String> errorCodes = ((BaseUncheckedException) e).getCodes();
			List<String> errorTexts = ((BaseUncheckedException) e).getErrorTexts();

			List<ErrorDTO> errors = errorTexts.parallelStream()
					.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
					.collect(Collectors.toList());

			response.setError(errors.get(0));
		}

		response.setTimestamp(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		response.setVersion("1.0");
		response.setResponse(null);
		Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(PacketReceiverResponseDTO.class, new PacketReceiverReqRespJsonSerializer()).create();
		return gson.toJson(response);
	}

	/**
	 * Handler.
	 *
	 * @param exe the exe
	 * @return the string
	 */
	public String handler(Throwable exe) {
		if(exe instanceof ValidationException)
			return handleValidationException((ValidationException) exe);
		if(exe instanceof UnexpectedException)
			return handleUnexpectedException((UnexpectedException)exe);
		if(exe instanceof TimeoutException)
			return handleTimeoutException((TimeoutException)exe);
		if(exe instanceof TablenotAccessibleException)
			return handleTablenotAccessibleException((TablenotAccessibleException)exe);
		if(exe instanceof PacketNotSyncException)
			return handlePacketNotSyncException((PacketNotSyncException)exe);
		if(exe instanceof FileSizeExceedException)
			return handleFileSizeExceedException((FileSizeExceedException)exe);
		if(exe instanceof PacketNotValidException)
			return handlePacketNotValidException((PacketNotValidException)exe);
		if(exe instanceof DuplicateUploadRequestException)
			return duplicateentry((DuplicateUploadRequestException)exe);
		if(exe instanceof MissingServletRequestPartException)
			return handlePacketNotAvailableException((MissingServletRequestPartException)exe);
		else return dataExceptionHandler((DataIntegrityViolationException) exe);
	}



}