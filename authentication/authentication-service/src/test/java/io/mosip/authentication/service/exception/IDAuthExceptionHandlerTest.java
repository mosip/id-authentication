package io.mosip.authentication.service.exception;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.ActionableAuthError;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.BaseAuthResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.util.DataValidationUtil;

/**
 * @author Manoj SP
 *
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IDAuthExceptionHandlerTest {
    @Autowired
    Environment environment;

    @Autowired
    ObjectMapper mapper;

    @InjectMocks
    private IdAuthExceptionHandler handler;

    @Before
    public void before() {
	ResourceBundleMessageSource source = new ResourceBundleMessageSource();
	source.setBasename("errormessages");
	ReflectionTestUtils.setField(handler, "messageSource", source);
	ReflectionTestUtils.setField(handler, "mapper", mapper);
    }

    @Test
    public void testHandleAllException() {
	ResponseEntity<Object> handleAllExceptions = handler
		.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
	BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleAllExceptions.getBody();
	List<AuthError> errorCode = response.getErr();
	errorCode.forEach(e -> {
	    assertEquals("IDA-MLC-101", e.getErrorCode());
	    assertEquals("Unknown error occured", e.getErrorMessage());
	});
    }

    @Test
    public void testHandleExceptionInternal() {
	ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
		new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null,
		HttpStatus.EXPECTATION_FAILED, null);
	BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	List<AuthError> errorCode = response.getErr();
	errorCode.forEach(e -> {
	    assertEquals("IDA-RQV-101", e.getErrorCode());
	    assertEquals("Invalid Auth Request", e.getErrorMessage());
	});
    }

    @Test
    public void testHandleIdAppException() {
	ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(
		new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED), null);
	BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleIdAppException.getBody();
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
	BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleIdAppException.getBody();
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
	BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	response.getErr();
    }

    @Test
    public void testHandleDataException() {
	BaseAuthResponseDTO expectedResponse = new BaseAuthResponseDTO();
	expectedResponse.setStatus("N");
	expectedResponse.setTxnID(null);
	expectedResponse.setErr(Collections
		.singletonList(new AuthError(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorCode(),
			IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorMessage())));

	Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
	errors.reject(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorCode(),
		IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorMessage());
	try {
	    DataValidationUtil.validate(errors);
	} catch (IDDataValidationException e) {
	    ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
		    new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e), null);
	    BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	    actualResponse.setResTime(null);
	    assertEquals(expectedResponse, actualResponse);
	}
    }

    @Test
    public void testAsyncRequestTimeoutException() {
	BaseAuthResponseDTO expectedResponse = new BaseAuthResponseDTO();
	expectedResponse.setStatus("N");
	expectedResponse.setTxnID(null);
	expectedResponse.setErr(Collections
		.singletonList(new AuthError(IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT.getErrorCode(),
			IdAuthenticationErrorConstants.CONNECTION_TIMED_OUT.getErrorMessage())));
	AsyncRequestTimeoutException e = new AsyncRequestTimeoutException();
	ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(e, null, null, null, null);
	BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	actualResponse.setResTime(null);
	assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testNoSuchMessageException() {
	BaseAuthResponseDTO expectedResponse = new BaseAuthResponseDTO();
	expectedResponse.setStatus("N");
	expectedResponse.setTxnID(null);
	expectedResponse.setErr(
		Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorCode(),
			IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorMessage())));
	ResponseEntity<Object> handleExceptionInternal = handler
		.handleIdAppException(new IdAuthenticationAppException("1234", "1234"), null);
	BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	actualResponse.setResTime(null);
	assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testhandleAllExceptionsUnknownError() {
	BaseAuthResponseDTO expectedResponse = new BaseAuthResponseDTO();
	expectedResponse.setStatus("N");
	expectedResponse.setTxnID(null);
	expectedResponse.setErr(
		Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorCode(),
			IdAuthenticationErrorConstants.UNKNOWN_ERROR.getErrorMessage())));

	Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
	errors.reject(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorCode(),
		IdAuthenticationErrorConstants.AUTHENTICATION_FAILED.getErrorMessage());
	try {
	    DataValidationUtil.validate(errors);
	} catch (IDDataValidationException e) {
	    ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
		    new IdAuthenticationAppException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED, e), null,
		    null, null, null);
	    BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	    actualResponse.setResTime(null);
	    assertEquals(expectedResponse, actualResponse);
	}
    }

    @Test
    public void testCreateAuthError() {
	BaseAuthResponseDTO expectedResponse = new BaseAuthResponseDTO();
	expectedResponse.setStatus("N");
	expectedResponse.setTxnID(null);
	expectedResponse.setErr(Collections
		.singletonList(new ActionableAuthError(IdAuthenticationErrorConstants.INVALID_TXN_ID.getErrorCode(),
			IdAuthenticationErrorConstants.INVALID_TXN_ID.getErrorMessage(),
			IdAuthenticationErrorConstants.INVALID_TXN_ID.getActionCode())));
	ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
		new IdAuthenticationBaseException(IdAuthenticationErrorConstants.INVALID_TXN_ID), null);
	BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
	actualResponse.setResTime(null);
	assertEquals(expectedResponse, actualResponse);
    }
}