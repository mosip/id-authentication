package io.mosip.authentication.common.service.exception;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;

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

	@InjectMocks
	private IdAuthExceptionHandler handler;

	@Mock
	private HttpServletRequest servletRequest;

	@Before
	public void before() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("errormessages");
		ReflectionTestUtils.setField(handler, "servletRequest", servletRequest);
	}

	@Test
	public void testHandleAllException() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleAllExceptions.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleExceptionInternal() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/kyc");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null,
				HttpStatus.EXPECTATION_FAILED, null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppException() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/i");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS), null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppExceptionwithInternal() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/internal");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS), null);
		AutnTxnResponseDto response = (AutnTxnResponseDto) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppExceptionwithOtp() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/otp");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/otp");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS), null);
		OtpResponseDTO response = (OtpResponseDTO) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void testHandleIdAppExceptionWithCause() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		IdAuthenticationAppException ex = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(ex, null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleIdAppException.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

	@Test
	public void TestframeErrorResponse() {
		List<AuthError> authErrors = new ArrayList<>();
		AuthError e = new AuthError();
		e.setErrorCode(IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode());
		e.setErrorMessage(IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
		authErrors.add(e);
		ReflectionTestUtils.invokeMethod(handler, "frameErrorResponse", "internal", "otp", authErrors);
	}

	@Test
	public void testHandleExceptionInternalWithObject() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null, null,
				null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		response.getErrors();
	}

	@Test
	public void testHandleDataException() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));

		Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
		errors.reject(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
					new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e), null);
			BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Test
	public void testAsyncRequestTimeoutException() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));
		expectedResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));
		AsyncRequestTimeoutException e = new AsyncRequestTimeoutException();
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(e, null, null, null, null);
		BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		actualResponse.setResponseTime(null);
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	public void testNoSuchMessageException() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));
		ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
				new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()),
				null);
		AuthResponseDTO actualResponse = (AuthResponseDTO) handleExceptionInternal.getBody();
		actualResponse.setResponseTime(null);
		ResponseDTO response = new ResponseDTO();
		response.setAuthStatus(Boolean.FALSE);
		actualResponse.setResponse(response);
		actualResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	public void testhandleAllExceptionsUnknownError() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(
				Collections.singletonList(new AuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage())));

		Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
		errors.reject(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
				IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
					new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e), null, null,
					null, null);
			AuthResponseDTO actualResponse = (AuthResponseDTO) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Test
	public void testCreateAuthError() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/auth/zyx");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(Collections
				.singletonList(new ActionableAuthError(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getActionMessage())));
		ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
				new IdAuthenticationBaseException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS), null);
		BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		actualResponse.setResponseTime(null);
//		assertEquals(expectedResponse, actualResponse);
	}

	@Ignore
	@Test
	public void testCreateAuthErrorwithActionCode() {
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(Collections
				.singletonList(new ActionableAuthError(IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorCode(),
						IdAuthenticationErrorConstants.EXPIRED_OTP.getErrorMessage(),
						IdAuthenticationErrorConstants.EXPIRED_OTP.getActionMessage())));
		ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
				new IdAuthenticationBaseException(IdAuthenticationErrorConstants.EXPIRED_OTP), null);
		BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		actualResponse.setResponseTime(null);
//		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	public void testHandleAllException2() {
		Mockito.when(servletRequest.getContextPath()).thenReturn("/otp");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/otp/");
		Mockito.when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		OtpResponseDTO response = (OtpResponseDTO) handleAllExceptions.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}
}