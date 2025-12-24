package io.mosip.authentication.common.service.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.authtype.dto.AuthtypeResponseDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.util.DataValidationUtil;

/**
 * @author Manoj SP
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Import(EnvUtil.class)
public class IDAuthExceptionHandlerTest {
	@Autowired
	EnvUtil environment;

	@InjectMocks
	private IdAuthExceptionHandler handler;

	@Mock
	private HttpServletRequest servletRequest;

    @InjectMocks
    private IdAuthExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

	@Before
	public void before() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("errormessages");
		ReflectionTestUtils.setField(handler, "servletRequest", servletRequest);
	}

	@Test
	public void testHandleAllException() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/kyc");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/i");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/internal");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/authTransactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/otp");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/otp");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null, null,
				null);
		BaseAuthResponseDTO response = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
		response.getErrors();
	}

	@Test
	public void testHandleDataException() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
	public void testHandleDataExceptionInternalAuthTxn() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/internal");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
	public void testHandleDataExceptionInternalOtp() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/internal");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/otp/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		OtpResponseDTO expectedResponse = new OtpResponseDTO();
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
			OtpResponseDTO actualResponse = (OtpResponseDTO) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Test
	public void testHandleDataExceptionInternalAuthType() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/internal");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/authtypes");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		AuthtypeResponseDto expectedResponse = new AuthtypeResponseDto();
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
			AuthtypeResponseDto actualResponse = (AuthtypeResponseDto) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Test
	public void testHandleDataExceptionWithArgs() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(Collections
				.singletonList(new AuthError(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage())));

		Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
		errors.reject(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] { "bioType" },
				IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
					new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, e), null);
			BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertTrue(!actualResponse.getErrors().isEmpty());
		}
	}

	@Test
	public void testHandleDataExceptionWithArgsAndActionArgs() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		AuthResponseDTO expectedResponse = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.FALSE);
		expectedResponse.setResponse(res);
		expectedResponse.setErrors(Collections
				.singletonList(new AuthError(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
						IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage())));

		Errors errors = new BindException(expectedResponse, "BaseAuthResponseDTO");
		errors.reject(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
				new Object[] { "Email" }, IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage());
		try {
			DataValidationUtil.validate(errors);
		} catch (IDDataValidationException e) {
			ResponseEntity<Object> handleExceptionInternal = handler.handleIdAppException(
					new IdAuthenticationAppException(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED, e),
					null);
			BaseAuthResponseDTO actualResponse = (BaseAuthResponseDTO) handleExceptionInternal.getBody();
			actualResponse.setResponseTime(null);
			assertTrue(!actualResponse.getErrors().isEmpty());
		}
	}

	@Test
	public void testAsyncRequestTimeoutException() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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
		lenient().when(servletRequest.getContextPath()).thenReturn("/auth/zyx");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/auth-transactions/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
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

	@Test
	public void testHandleAllException2() {
		lenient().when(servletRequest.getContextPath()).thenReturn("/otp");
		StringBuffer value = new StringBuffer();
		value.append("http://localhost:8093/idauthentication/v1/internal/otp/");
		lenient().when(servletRequest.getRequestURL()).thenReturn(value);
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		OtpResponseDTO response = (OtpResponseDTO) handleAllExceptions.getBody();
		List<AuthError> errorCode = response.getErrors();
		errorCode.forEach(e -> {
			assertEquals("IDA-MLC-007", e.getErrorCode());
			assertEquals("Request could not be processed. Please try again", e.getErrorMessage());
		});
	}

    @Test
    public void testHandleAllExceptionsReturnsUnknownException() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));

        Exception ex = new Exception("Test");
        ResponseEntity<Object> response = exceptionHandler.handleAllExceptions(ex, webRequest);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    // ---------- handleExceptionInternal / notify InvalidFormatException eventType ----------
    @Test
    public void testHandleExceptionInternalNotifyEventType() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/notify"));
        InvalidFormatException cause = InvalidFormatException.from(null, "eventType", null, null);
        HttpMessageConversionException ex = new HttpMessageConversionException("Test", cause);

        ResponseEntity<Object> response = exceptionHandler.handleExceptionInternal(ex, null, null, null, webRequest);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    // ---------- handleExceptionInternal / notify InvalidFormatException expiryTimestamp ----------
    @Test
    public void testHandleExceptionInternalNotifyExpiryTimestamp() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/notify"));
        InvalidFormatException cause = InvalidFormatException.from(null, "expiryTimestamp", null, null);
        HttpMessageConversionException ex = new HttpMessageConversionException("Test", cause);

        ResponseEntity<Object> response = exceptionHandler.handleExceptionInternal(ex, null, null, null, webRequest);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    // ---------- handleExceptionInternal / ServletException ----------
    @Test
    public void testHandleExceptionInternalServletException() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        ResponseEntity<Object> response = exceptionHandler.handleExceptionInternal(new ServletRequestBindingException("Test"), null, null, null, webRequest);
        assertNotNull(response.getBody());
    }

    // ---------- handleExceptionInternal / HttpMessageConversionException ----------
    @Test
    public void testHandleExceptionInternalHttpMessageConversionException() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        ResponseEntity<Object> response = exceptionHandler.handleExceptionInternal(new HttpMessageConversionException("Test"), null, null, null, webRequest);
        assertNotNull(response.getBody());
    }

    // ---------- handleExceptionInternal / AsyncRequestTimeoutException ----------
    @Test
    public void testHandleExceptionInternalAsyncRequestTimeoutException() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        ResponseEntity<Object> response = exceptionHandler.handleExceptionInternal(new AsyncRequestTimeoutException(), null, null, null, webRequest);
        assertNotNull(response.getBody());
    }

    // ---------- buildExceptionResponse ----------
    @Test
    public void testBuildExceptionResponseWithEmptyErrors() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        Exception ex = new Exception("Test");
        Object response = IdAuthExceptionHandler.buildExceptionResponse(ex, servletRequest, Collections.emptyList());
        assertNull(response);
    }
}