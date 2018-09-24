package org.mosip.auth.service.exception;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.auth.core.dto.indauth.AuthError;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@RunWith(MockitoJUnitRunner.class)
public class IDAuthenticationExceptionHandlerTest {

	@Mock
	private Errors errors;

	private IDAuthenticationExceptionHandler handler;

	@Before
	public void setUp() {
		handler = new IDAuthenticationExceptionHandler();
	}

	@Test
	public void testHandleAllException() {
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		AuthResponseDTO response = (AuthResponseDTO) handleAllExceptions.getBody();
		List<AuthError> errorCode = response.getErrorCode();
		errorCode.forEach(e -> {
			assertEquals("IDA-DTV-IDV-004", e.getErrorCode());
			assertEquals("Unknown error occured", e.getErrorMessage());
		});

	}

	@Test
	public void testHandleExceptionInternal() {
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"),
				Arrays.asList(new String[] { "Media type is not supported" }), null, HttpStatus.EXPECTATION_FAILED,
				null);
		AuthResponseDTO response = (AuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErrorCode();
		errorCode.forEach(e -> {
			assertEquals("Http Media Type Not Supported Exception", e.getErrorCode());
			assertEquals("Media type is not supported", e.getErrorMessage());
		});
	}

	//FIXME check if below is required
//	@Test
//	public void testGenerateOtpExcpetion() {
//		ResponseEntity<Object> handleIdUsageException = handler
//				.handleIdUsageException(new GenerateOTPException(IdUsageExceptions.ID_EXPIRED_EXCEPTION), null);
//		AuthResponseDTO response = (AuthResponseDTO) handleIdUsageException.getBody();
//		List<AuthError> errorCode = response.getErrorCode();
//		errorCode.forEach(e -> {
//			assertEquals("IDA-DTV-IDV-003", e.getErrorCode());
//			assertEquals("Expired VID is expired", e.getErrorMessage());
//		});
//	}
//
//	@Test
//	public void testValidateOtpException() {
//		ResponseEntity<Object> handleIdUsageException = handler
//				.handleIdUsageException(new ValidateOTPException(IdUsageExceptions.ID_EXPIRED_EXCEPTION), null);
//		AuthResponseDTO response = (AuthResponseDTO) handleIdUsageException.getBody();
//		List<AuthError> errorCode = response.getErrorCode();
//		errorCode.forEach(e -> {
//			assertEquals("IDA-DTV-IDV-003", e.getErrorCode());
//			assertEquals("Expired VID is expired", e.getErrorMessage());
//		});
//	}

	@Test
	public void testValidateOtpExceptionWithErrorObject() {
		ResponseEntity<Object> handleIdUsageException = handler
				.handleExceptionInternal(new AsyncRequestTimeoutException(), null, null, null, null);
		AuthResponseDTO response = (AuthResponseDTO) handleIdUsageException.getBody();
		List<AuthError> errorCode = response.getErrorCode();
		errorCode.forEach(e -> {
			assertEquals("IDA-DTV-IDV-004", e.getErrorCode());
			assertEquals("Unknown error occured", e.getErrorMessage());
		});
	}
}