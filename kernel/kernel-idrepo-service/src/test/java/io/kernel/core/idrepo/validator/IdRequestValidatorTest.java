package io.kernel.core.idrepo.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.idrepo")
public class IdRequestValidatorTest {

	@InjectMocks
	IdRequestValidator validator;

	@Autowired
	private Environment env;

	private Map<String, String> id;

	private Map<String, String> status;

	@Autowired
	ObjectMapper mapper;

	@Mock
	private UinValidatorImpl uinValidator;

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	Errors errors;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(validator, "id", id);
		ReflectionTestUtils.setField(validator, "status", status);
		ReflectionTestUtils.setField(validator, "env", env);
		ReflectionTestUtils.setField(validator, "uinValidator", uinValidator);
		errors = new BeanPropertyBindingResult(new IdRequestDTO(), "idRequestDto");
	}

	@Test
	public void testSupport() {
		assertTrue(validator.supports(IdRequestDTO.class));
	}

	@Test
	public void testValidateIdNullId() {
		ReflectionTestUtils.invokeMethod(validator, "validateId", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "id"),
					error.getDefaultMessage());
			assertEquals("id", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateIdInvalidId() {
		ReflectionTestUtils.invokeMethod(validator, "validateId", "abc", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "id"),
					error.getDefaultMessage());
			assertEquals("id", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateIdNullVer() {
		ReflectionTestUtils.invokeMethod(validator, "validateVer", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "ver"),
					error.getDefaultMessage());
			assertEquals("ver", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateIdInvalidVer() {
		ReflectionTestUtils.invokeMethod(validator, "validateVer", "abc", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "ver"),
					error.getDefaultMessage());
			assertEquals("ver", ((FieldError) error).getField());
		});
	}

	@Test
	public void testNullUin() {
		ReflectionTestUtils.invokeMethod(validator, "validateUin", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "uin"),
					error.getDefaultMessage());
			assertEquals("uin", ((FieldError) error).getField());
		});
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		ReflectionTestUtils.invokeMethod(validator, "validateUin", "1234", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorCode(), error.getCode());
			assertEquals(IdRepoErrorConstants.INVALID_UIN.getErrorMessage(), error.getDefaultMessage());
			assertEquals("uin", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateStatusNullStatus() {
		ReflectionTestUtils.invokeMethod(validator, "validateStatus", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "status"),
					error.getDefaultMessage());
			assertEquals("status", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateStatusInvalidStatus() {
		ReflectionTestUtils.invokeMethod(validator, "validateStatus", "1234", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "status"),
					error.getDefaultMessage());
			assertEquals("status", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRegIdNullRegId() {
		ReflectionTestUtils.invokeMethod(validator, "validateRegId", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(
					String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "registrationId"),
					error.getDefaultMessage());
			assertEquals("registrationId", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestNullRequest() {
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "request"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateReqTimeNullReqTime() {
		ReflectionTestUtils.invokeMethod(validator, "validateReqTime", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "timestamp"),
					error.getDefaultMessage());
			assertEquals("timestamp", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateReqTimeInvalidReqTime() {
		ReflectionTestUtils.invokeMethod(validator, "validateReqTime", "1234", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "timestamp"),
					error.getDefaultMessage());
			assertEquals("timestamp", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidate() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		request.setVer("1.0");
		request.setRegistrationId("1234");
		request.setUin("1234");
		request.setStatus("REGISTERED");
		request.setTimestamp(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		request.setRequest(mapper.readValue(mapper.writeValueAsBytes(request), Object.class));
		validator.validate(request, errors);
		assertFalse(errors.hasErrors());
	}
}
