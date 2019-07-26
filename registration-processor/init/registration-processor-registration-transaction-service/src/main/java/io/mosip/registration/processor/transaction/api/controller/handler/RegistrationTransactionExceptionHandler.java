package io.mosip.registration.processor.transaction.api.controller.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.exception.TransactionsUnavailableException;
import io.mosip.registration.processor.status.sync.response.dto.RegTransactionResponseDTO;
import io.mosip.registration.processor.transaction.api.controller.RegistrationTransactionController;

@RestControllerAdvice(assignableTypes=RegistrationTransactionController.class)
public class RegistrationTransactionExceptionHandler {

	private static final String REG_TRANSACTION_SERVICE_ID = "mosip.registration.processor.registration.transaction.id";
	private static final String REG_TRANSACTION_APPLICATION_VERSION = "mosip.registration.processor.transaction.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	
	@Autowired
	private Environment env;
	
	@Value("${registration.processor.signature.isEnabled}")
	Boolean isEnabled;
	
	@Autowired
	DigitalSignatureUtility digitalSignatureUtility;
	
	private static final String RESPONSE_SIGNATURE = "Response-Signature";

	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationTransactionExceptionHandler.class);

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<RegTransactionResponseDTO> accessDenied(AccessDeniedException e) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				e.getErrorCode(), e.getMessage());
		return buildRegTransactionExceptionResponse((Exception) e);
	}	
	
	@ExceptionHandler(TransactionTableNotAccessibleException.class)
	public ResponseEntity<RegTransactionResponseDTO> handleTransactionTableNotAccessibleException(TransactionTableNotAccessibleException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getMessage());
		return buildRegTransactionExceptionResponse((Exception)e);
	}
	
	@ExceptionHandler(TransactionsUnavailableException.class)
	public ResponseEntity<RegTransactionResponseDTO> handleTransactionsUnavailableException(TransactionsUnavailableException e, WebRequest request) {
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),LoggerFileConstant.APPLICATIONID.toString(),e.getErrorCode(), e.getMessage());
		return buildRegTransactionExceptionResponse((Exception)e);
	}
	
	@ExceptionHandler(RegStatusAppException.class)
	protected ResponseEntity<RegTransactionResponseDTO> handleRegStatusException(RegStatusAppException e, WebRequest request) {
		return buildRegTransactionExceptionResponse((Exception)e);

	}
	
	@ExceptionHandler(InvalidTokenException.class)
	protected ResponseEntity<RegTransactionResponseDTO> handleInvalidTokenException(InvalidTokenException e, WebRequest request) {
		return buildRegTransactionExceptionResponse((Exception)e);

	}
	
	private ResponseEntity<RegTransactionResponseDTO> buildRegTransactionExceptionResponse(Exception ex) {
		RegTransactionResponseDTO response = new RegTransactionResponseDTO();
		Throwable e = ex;

		if (Objects.isNull(response.getId())) {
			response.setId(env.getProperty(REG_TRANSACTION_SERVICE_ID));
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
		response.setVersion(env.getProperty(REG_TRANSACTION_APPLICATION_VERSION));
		response.setResponse(null);
		Gson gson = new GsonBuilder().create();

		if(isEnabled) {
			HttpHeaders headers = new HttpHeaders();
			headers.add(RESPONSE_SIGNATURE,digitalSignatureUtility.getDigitalSignature(gson.toJson(response)));
			return ResponseEntity.ok().headers(headers).body(response);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
