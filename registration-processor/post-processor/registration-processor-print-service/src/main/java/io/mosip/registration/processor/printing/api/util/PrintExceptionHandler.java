package io.mosip.registration.processor.printing.api.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.print.service.exception.RegPrintAppException;
import io.mosip.registration.processor.printing.api.controller.PrintApiController;
import io.mosip.registration.processor.printing.api.dto.PrintResponse;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;

/**
 * The Class PrintExceptionHandler.
 * 
 * @author M1048358 Alok
 */
@RestControllerAdvice(assignableTypes = PrintApiController.class)
public class PrintExceptionHandler {

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PRINT_SERVICE_ID = "mosip.registration.processor.print.service.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PRINT_SERVICE_VERSION = "mosip.registration.processor.application.version";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The env. */
	@Autowired
	private Environment env;

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintExceptionHandler.class);

	/**
	 * Reg print app exception.
	 *
	 * @param e
	 *            the e
	 * @return the response entity
	 */
	@ExceptionHandler(RegPrintAppException.class)
	public ResponseEntity<PrintResponse> regPrintAppException(RegPrintAppException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	/**
	 * Reg print app exception.
	 *
	 * @param e
	 *            the e
	 * @return the response entity
	 */
	@ExceptionHandler(PDFGeneratorException.class)
	public ResponseEntity<PrintResponse> pdfgeneratorException(PDFGeneratorException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	/**
	 * Reg print app exception.
	 *
	 * @param e
	 *            the e
	 * @return the response entity
	 */
	@ExceptionHandler(TemplateProcessingFailureException.class)
	public ResponseEntity<PrintResponse> templateFailureException(TemplateProcessingFailureException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	@ExceptionHandler(TablenotAccessibleException.class)
	public ResponseEntity<PrintResponse> duplicateEntry(TablenotAccessibleException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	@ExceptionHandler(JsonMappingException.class)
	public ResponseEntity<PrintResponse> badRequest(JsonMappingException ex) {
		RegStatusAppException reg1 = new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_MAPPING_EXCEPTION,
				ex);
		return buildPrintApiExceptionResponse(reg1);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<PrintResponse> badRequest(InvalidFormatException ex) {
		RegStatusAppException reg1 = new RegStatusAppException(
				PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
				String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), "idType"));
		return buildPrintApiExceptionResponse(reg1);
	}

	@ExceptionHandler(JsonParseException.class)
	public ResponseEntity<PrintResponse> badRequest(JsonParseException ex) {
		RegStatusAppException reg1 = new RegStatusAppException(PlatformErrorMessages.RPR_RGS_JSON_PARSING_EXCEPTION,
				ex);
		return buildPrintApiExceptionResponse(reg1);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<PrintResponse> badRequest(MethodArgumentNotValidException ex) {
		return buildPrintApiExceptionResponse((Exception) ex);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<PrintResponse> dataExceptionHandler(final DataIntegrityViolationException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<PrintResponse> accessDenied(AccessDeniedException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<PrintResponse> invalidToken(InvalidTokenException e) {
		return buildPrintApiExceptionResponse((Exception) e);
	}

	/**
	 * Builds the reg status exception response.
	 *
	 * @param ex
	 *            the ex
	 * @return the response entity
	 */
	private ResponseEntity<PrintResponse> buildPrintApiExceptionResponse(Exception ex) {
		PrintResponse response = new PrintResponse();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_PRINT_SERVICE_ID));
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
		response.setVersion(env.getProperty(REG_PRINT_SERVICE_VERSION));

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
	}

}