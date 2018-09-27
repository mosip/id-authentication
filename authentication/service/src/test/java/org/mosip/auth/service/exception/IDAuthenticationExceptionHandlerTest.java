package org.mosip.auth.service.exception;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthError;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(value = { "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })
public class IDAuthenticationExceptionHandlerTest {
	@Autowired
	Environment environment;

	@Mock
	private Errors errors;

	private IdAuthExceptionHandler handler;

	@Before
	public void setUp() {
		handler = new IdAuthExceptionHandler();
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(environment.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(environment.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(environment.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(environment.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(environment.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.invokeMethod(handler, "initializeLogger", mosipRollingFileAppender);
	}

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
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"),
				Arrays.asList(new String[] { "Media type is not supported" }), null, HttpStatus.EXPECTATION_FAILED,
				null);
		AuthResponseDTO response = (AuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("Http Media Type Not Supported Exception", e.getErrorCode());
			assertEquals("Media type is not supported", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppException() {
		ResponseEntity<Object> handleIdAppException = handler
				.handleIdAppException(new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED), null);
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
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"),
				null, null, null,
				null);
		AuthResponseDTO response = (AuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErr();
	}
}