package io.mosip.kernel.idrepo.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.idrepo.dto.ErrorDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;

/**
 * The Class IdRepoExceptionHandlerTest.
 *
 * @author Manoj SP
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoExceptionHandlerTest {

	@Autowired
	Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The errors. */
	@Mock
	private Errors errors;

	/** The handler. */
	@InjectMocks
	private IdRepoExceptionHandler handler;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(handler, "env", env);
		ReflectionTestUtils.setField(handler, "mapper", mapper);
	}

	/**
	 * Test handle all exception.
	 */
	@Test
	public void testHandleAllException() {
		ResponseEntity<Object> handleAllExceptions = ReflectionTestUtils.invokeMethod(handler, "handleAllExceptions",
				new RuntimeException("Runtime Exception"), null);
		IdResponseDTO response = (IdResponseDTO) handleAllExceptions.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.UNKNOWN_ERROR.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.UNKNOWN_ERROR.getErrorMessage(), e.getErrMessage());
		});
	}

	/**
	 * Test handle exception internal.
	 */
	@Test
	public void testHandleExceptionInternal() {
		ResponseEntity<Object> handleExceptionInternal = ReflectionTestUtils.invokeMethod(handler,
				"handleExceptionInternal",
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null,
				HttpStatus.EXPECTATION_FAILED, null);
		IdResponseDTO response = (IdResponseDTO) handleExceptionInternal.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.INVALID_REQUEST.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage(), e.getErrMessage());
		});
	}

	/**
	 * Test handle id app exception.
	 */
	@Test
	public void testHandleIdAppException() {
		ResponseEntity<Object> handleIdAppException = ReflectionTestUtils.invokeMethod(handler, "handleIdAppException",
				new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN), null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppException.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), e.getErrMessage());
		});
	}

	/**
	 * Test handle id app exception with cause.
	 */
	@Test
	public void testHandleIdAppExceptionWithCause() {
		IdRepoAppException ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN,
				new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, "mosip.id.create"), "mosip.id.create");
		ResponseEntity<Object> handleIdAppException = ReflectionTestUtils.invokeMethod(handler, "handleIdAppException",
				ex, null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppException.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), e.getErrMessage());
		});
	}
	
	
	@Test
	public void testHandleIdAppExceptionWithUncheckedCause() {
		IdRepoAppUncheckedException ex = new IdRepoAppUncheckedException(IdRepoErrorConstants.INVALID_UIN,
				new IdRepoAppUncheckedException(IdRepoErrorConstants.INVALID_UIN, "mosip.id.create"), "mosip.id.create");
		ResponseEntity<Object> handleIdAppException = ReflectionTestUtils.invokeMethod(handler, "handleIdAppUncheckedException",
				ex, null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppException.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), e.getErrMessage());
		});
	}

	@Test
	public void testHandleIdAppUncheckedException() {
		IdRepoAppUncheckedException ex = new IdRepoAppUncheckedException(IdRepoErrorConstants.INVALID_UIN,
				new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN, "mosip.id.create"), "mosip.id.create");
		ResponseEntity<Object> handleIdAppUncheckedException = ReflectionTestUtils.invokeMethod(handler,
				"handleIdAppUncheckedException", ex, null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppUncheckedException.getBody();
		List<ErrorDTO> errorCode = response.getError();
		errorCode.forEach(e -> {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), e.getErrCode());
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), e.getErrMessage());
		});
	}

	/**
	 * Test handle exception internal with object.
	 */
	@Test
	public void testHandleExceptionInternalWithObject() {
		ResponseEntity<Object> handleExceptionInternal = ReflectionTestUtils.invokeMethod(handler,
				"handleExceptionInternal",
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null, null,
				null);
		IdResponseDTO response = (IdResponseDTO) handleExceptionInternal.getBody();
		response.getError();
	}

	@Test
	public void testHandleExceptionInternalWithOtherException() {
		ResponseEntity<Object> handleExceptionInternal = ReflectionTestUtils.invokeMethod(handler,
				"handleExceptionInternal", new IdRepoAppException(), null, null, null, null);
		IdResponseDTO response = (IdResponseDTO) handleExceptionInternal.getBody();
		response.getError();
	}
}
