package io.mosip.registration.processor.manual.verification.exception.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.fsadapter.hdfs.constant.HDFSAdapterErrorCode;
import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.manual.verification.exception.FileNotFoundInFileStoreException;
import io.mosip.registration.processor.manual.verification.exception.FileNotPresentException;
import io.mosip.registration.processor.manual.verification.exception.InvalidFieldsException;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidTokenHandlerException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationAppException;
import io.mosip.registration.processor.manual.verification.exception.MatchTypeNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.verification.exception.PacketNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.UserIDNotPresentException;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationAssignResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.vertx.core.json.DecodeException;

/**
 * The Class ManualAssignDecisionExceptionHandler.
 * 
 * @author Rishabh Keshari
 */
public class ManualVerificationExceptionHandler {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ManualVerificationExceptionHandler.class);

	/** The Constant APPLICATION_VERSION. */
	private static final String APPLICATION_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	/** The id. */
	private String id = "";
	Object responseDtoType;
	/** The env. */
	@Autowired
	private Environment env;

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
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Instantiates a new manual assign decision exception handler.
	 */
	public ManualVerificationExceptionHandler() {

	}

	public Object getResponseDtoType() {
		return responseDtoType;
	}

	public void setResponseDtoType(Object responseDtoType) {
		this.responseDtoType = responseDtoType;
	}

	/**
	 * Instantiates a new manual assign decision exception handler.
	 *
	 * @param id
	 *            the id
	 */
	public ManualVerificationExceptionHandler(String id) {
		this.id = id;
	}

	/**
	 * Invalid file name exception handler.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO invalidFileNameExceptionHandler(final InvalidFileNameException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	/**
	 * Packet not found exception handler.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO packetNotFoundExceptionHandler(final PacketNotFoundException e) {
		FileNotPresentException fileNotPresentException = new FileNotPresentException(
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getCode(),
				PlatformErrorMessages.RPR_MVS_FILE_NOT_PRESENT.getMessage());
		return buildAssignDecisionExceptionResponse(fileNotPresentException);
	}

	/**
	 * No record assigned exception handler.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO noRecordAssignedExceptionHandler(NoRecordAssignedException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	/**
	 * No record assigned exception handler.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO UserIDNotPresentException(UserIDNotPresentException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	/**
	 * Data exception handler.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO decodeExceptionHandler(DecodeException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-001 JSON DATA DECODE exception", e.getMessage());
		ManualVerificationAppException ex = new ManualVerificationAppException(
				PlatformErrorMessages.RPR_MVS_DECODE_EXCEPTION, e);
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	public BaseRestResponseDTO matchTypeNotFoundExceptionHandler(MatchTypeNotFoundException e) {
		MatchTypeNotFoundException ex = new MatchTypeNotFoundException(
				PlatformErrorMessages.RPR_MVS_NO_MATCH_TYPE_PRESENT.getCode(),
				PlatformErrorMessages.RPR_MVS_NO_MATCH_TYPE_PRESENT.getMessage());
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	public BaseRestResponseDTO fileNotFoundInFileStoreExceptionHandler(FSAdapterException e) {
		FileNotFoundInFileStoreException ex = new FileNotFoundInFileStoreException(
				HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
				HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorMessage());
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	public BaseRestResponseDTO invalidTokenExceptionHandler(InvalidTokenException e) {
		InvalidTokenHandlerException ex = new InvalidTokenHandlerException(
				PlatformErrorMessages.RPR_AUT_INVALID_TOKEN.getCode(),
				PlatformErrorMessages.RPR_AUT_INVALID_TOKEN.getMessage());
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	public BaseRestResponseDTO unknownExceptionHandler(Exception e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				"RPR-DBE-001 JSON DATA DECODE exception", e.getMessage());
		ManualVerificationAppException ex = new ManualVerificationAppException(
				PlatformErrorMessages.RPR_MVS_UNKNOWN_EXCEPTION, e);
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	/**
	 * Invalid update exception.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO invalidUpdateException(InvalidUpdateException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	/**
	 * Invalid filed exception.
	 *
	 * @param e
	 *            the e
	 * @return the string
	 */
	public BaseRestResponseDTO invalidFiledException(InvalidFieldsException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	public BaseRestResponseDTO invalidManualVerificationAppException(ManualVerificationAppException e) {
		return buildAssignDecisionExceptionResponse((Exception) e);
	}

	public BaseRestResponseDTO invalidIllegalArgumentException(IllegalArgumentException e) {
		ManualVerificationAppException ex = new ManualVerificationAppException(
				PlatformErrorMessages.RPR_MVS_INVALID_ARGUMENT_EXCEPTION, e);
		return buildAssignDecisionExceptionResponse((Exception) ex);
	}

	/**
	 * Builds the packet receiver exception response.
	 *
	 * @param ex
	 *            the ex
	 * @return the string
	 */
	private BaseRestResponseDTO buildAssignDecisionExceptionResponse(Exception ex) {
		if (responseDtoType.getClass() == ManualVerificationAssignResponseDTO.class) {
			ManualVerificationAssignResponseDTO response = new ManualVerificationAssignResponseDTO();
			Throwable e = ex;

			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			if (e instanceof BaseCheckedException) {
				List<String> errorCodes = ((BaseCheckedException) e).getCodes();
				List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();
				List<ErrorDTO> errors = errorTexts.parallelStream()
						.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
						.collect(Collectors.toList());
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

			response.setResponsetime(DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
			response.setVersion("1.0");
			response.setResponse(null);
			return response;
		} else {
			ManualVerificationBioDemoResponseDTO response = new ManualVerificationBioDemoResponseDTO();
			Throwable e = ex;

			if (Objects.isNull(response.getId())) {
				response.setId(id);
			}
			if (e instanceof BaseCheckedException) {
				List<String> errorCodes = ((BaseCheckedException) e).getCodes();
				List<String> errorTexts = ((BaseCheckedException) e).getErrorTexts();
				List<ErrorDTO> errors = errorTexts.parallelStream()
						.map(errMsg -> new ErrorDTO(errorCodes.get(errorTexts.indexOf(errMsg)), errMsg)).distinct()
						.collect(Collectors.toList());
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
			response.setVersion(env.getProperty(APPLICATION_VERSION));
			response.setFile(null);
			return response;
		}

	}

	/**
	 * Handler.
	 *
	 * @param exe
	 *            the exe
	 * @return the string
	 */
	public BaseRestResponseDTO handler(Throwable exe) {
		if (exe instanceof InvalidFieldsException)
			return invalidFiledException((InvalidFieldsException) exe);
		if (exe instanceof InvalidUpdateException)
			return invalidUpdateException((InvalidUpdateException) exe);
		if (exe instanceof NoRecordAssignedException)
			return noRecordAssignedExceptionHandler((NoRecordAssignedException) exe);
		if (exe instanceof UserIDNotPresentException)
			return UserIDNotPresentException((UserIDNotPresentException) exe);
		if (exe instanceof PacketNotFoundException)
			return packetNotFoundExceptionHandler((PacketNotFoundException) exe);
		if (exe instanceof InvalidFileNameException)
			return invalidFileNameExceptionHandler((InvalidFileNameException) exe);
		if (exe instanceof ManualVerificationAppException)
			return invalidManualVerificationAppException((ManualVerificationAppException) exe);
		if (exe instanceof IllegalArgumentException)
			return invalidIllegalArgumentException((IllegalArgumentException) exe);
		if (exe instanceof DecodeException)
			return decodeExceptionHandler((DecodeException) exe);
		if (exe instanceof MatchTypeNotFoundException)
			return matchTypeNotFoundExceptionHandler((MatchTypeNotFoundException) exe);
		if (exe instanceof FSAdapterException) {
			return fileNotFoundInFileStoreExceptionHandler((FSAdapterException) exe);
		}
		if (exe instanceof InvalidTokenException) {
			return invalidTokenExceptionHandler((InvalidTokenException) exe);
		} else
			return unknownExceptionHandler((Exception) exe);
	}

}