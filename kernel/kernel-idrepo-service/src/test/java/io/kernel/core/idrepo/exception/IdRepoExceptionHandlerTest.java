package io.kernel.core.idrepo.exception;

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

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.dto.ErrorDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;

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

	/** The env. */
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
		ResponseEntity<Object> handleAllExceptions = handler
				.handleAllExceptions(new RuntimeException("Runtime Exception"), null);
		IdResponseDTO response = (IdResponseDTO) handleAllExceptions.getBody();
		List<ErrorDTO> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("KER-IDR-008", e.getErrCode());
			assertEquals("Unknown error occured", e.getErrMessage());
		});
	}

	/**
	 * Test handle exception internal.
	 */
	@Test
	public void testHandleExceptionInternal() {
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null,
				HttpStatus.EXPECTATION_FAILED, null);
		IdResponseDTO response = (IdResponseDTO) handleExceptionInternal.getBody();
		List<ErrorDTO> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("KER-IDR-007", e.getErrCode());
			assertEquals("Invalid Request", e.getErrMessage());
		});
	}

	/**
	 * Test handle id app exception.
	 */
	@Test
	public void testHandleIdAppException() {
		ResponseEntity<Object> handleIdAppException = handler
				.handleIdAppException(new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN), null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppException.getBody();
		List<ErrorDTO> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("KER-IDR-005", e.getErrCode());
			assertEquals("Invalid UIN", e.getErrMessage());
		});
	}

	/**
	 * Test handle id app exception with cause.
	 */
	@Test
	public void testHandleIdAppExceptionWithCause() {
		IdRepoAppException ex = new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN,
				new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN));
		ResponseEntity<Object> handleIdAppException = handler.handleIdAppException(ex, null);
		IdResponseDTO response = (IdResponseDTO) handleIdAppException.getBody();
		List<ErrorDTO> errorCode = response.getErr();
		errorCode.forEach(e -> {
			assertEquals("KER-IDR-005", e.getErrCode());
			assertEquals("Invalid UIN", e.getErrMessage());
		});
	}

	/**
	 * Test handle exception internal with object.
	 */
	@Test
	public void testHandleExceptionInternalWithObject() {
		ResponseEntity<Object> handleExceptionInternal = handler.handleExceptionInternal(
				new HttpMediaTypeNotSupportedException("Http Media Type Not Supported Exception"), null, null, null,
				null);
		IdResponseDTO response = (IdResponseDTO) handleExceptionInternal.getBody();
		response.getErr();
	}
}
