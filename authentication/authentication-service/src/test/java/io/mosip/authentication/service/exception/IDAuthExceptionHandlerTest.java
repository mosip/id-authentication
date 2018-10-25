package io.mosip.authentication.service.exception;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IDAuthExceptionHandlerTest {
	@Autowired
	Environment environment;

	@Mock
	private Errors errors;

	@InjectMocks
	private IdAuthExceptionHandler handler;

	@Test
	public void testHandleAllException() {
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		AuthResponseDTO response = (AuthResponseDTO) handleAllExceptions.getBody();
		List<AuthError> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("IDA-DTV-IDV-004", e.getErrorCode());
			assertEquals("Unknown error occured", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleExceptionInternal() {
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null,
				HttpStatus.EXPECTATION_FAILED, null);
		AuthResponseDTO response = (AuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("Http Media Type Not Supported Exception", e.getErrorCode());
			assertEquals("Http Media Type Not Supported Exception", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppException() {
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED), null);
		AuthResponseDTO response = (AuthResponseDTO) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("IDA-AUT-501", e.getErrorCode());
			assertEquals("Authentication failed", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppExceptionWithCause() {
		IdAuthenticationAppException ex = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.AUTHENTICATION_FAILED,
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED));
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(ex, null);
		AuthResponseDTO response = (AuthResponseDTO) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("IDA-AUT-501", e.getErrorCode());
			assertEquals("Authentication failed", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleExceptionInternalWithObject() {
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null, null,
				null);
		AuthResponseDTO response = (AuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErr();
	}
}