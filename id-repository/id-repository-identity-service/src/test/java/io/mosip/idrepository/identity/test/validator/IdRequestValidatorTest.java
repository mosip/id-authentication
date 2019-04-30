package io.mosip.idrepository.identity.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.core.dto.RequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.identity.validator.IdRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.jsonvalidator.exception.ConfigServerConnectionException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.exception.NullJsonSchemaException;
import io.mosip.kernel.core.jsonvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

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

	List<String> status;
	
	List<String> allowedTypes;

	public List<String> getAllowedTypes() {
		return allowedTypes;
	}

	public void setAllowedTypes(List<String> allowedTypes) {
		this.allowedTypes = allowedTypes;
	}

	@Autowired
	ObjectMapper mapper;

	@Mock
	private UinValidatorImpl uinValidator;

	@Mock
	private JsonValidatorImpl jsonValidator;

	@Mock
	private RidValidatorImpl ridValidator;

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	Errors errors;

	@Before
	public void setup() {
		status.add(env.getProperty("mosip.idrepo.status.registered"));
		allowedTypes.add("bio,demo,all");
		ReflectionTestUtils.setField(validator, "id", id);
		ReflectionTestUtils.setField(validator, "status", status);
		ReflectionTestUtils.setField(validator, "env", env);
		ReflectionTestUtils.setField(validator, "allowedTypes", allowedTypes);
		ReflectionTestUtils.setField(validator, "mapper", mapper);
		ReflectionTestUtils.setField(validator, "validation",
				Stream.of(new String[][] { { "identity.dateOfBirth", "^\\d{4}/([0]\\d|1[0-2])/([0-2]\\d|3[01])$" },
						{ "identity.fullName.*.language", "^[(?i)a-z]{2}$" }, })
						.collect(Collectors.toMap(data -> data[0], data -> data[1])));
		errors = new BeanPropertyBindingResult(new IdRequestDTO(), "idRequestDto");
	}

	@Test
	public void testSupport() {
		assertTrue(validator.supports(IdRequestDTO.class));
	}

	@Test
	public void testValidateRequestJsonAttributes() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.anyString())).thenReturn(new ValidationReport(true, null));
		Object request = mapper.readValue(
				"{\"identity\":{\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateStatusInvalidStatus() {
		ReflectionTestUtils.invokeMethod(validator, "validateStatus", "1234", errors, "update");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "status"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRegIdValidRegId() {
		when(ridValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(validator, "validateRegId", "1234", errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidateRegIdInvalidRegId() {
		when(ridValidator.validateId(Mockito.anyString()))
				.thenThrow(new InvalidIDException("errorCode", "errorMessage"));
		ReflectionTestUtils.invokeMethod(validator, "validateRegId", "1234", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(
					String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "registrationId"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
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
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateVerNullVer() {
		ReflectionTestUtils.invokeMethod(validator, "validateVersion", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "version"),
					error.getDefaultMessage());
			assertEquals("version", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateVerInvalidVer() {
		ReflectionTestUtils.invokeMethod(validator, "validateVersion", "1234.a", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "version"),
					error.getDefaultMessage());
			assertEquals("version", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestInvalidSchema() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any()))
				.thenThrow(new NullJsonSchemaException("errorCode", "errorMessage"));
		Object request = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.JSON_SCHEMA_PROCESSING_FAILED.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.JSON_SCHEMA_PROCESSING_FAILED.getErrorMessage(), "request"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestWithDocuments() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any())).thenReturn(null);
		Object request = mapper.readValue(
				"{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":795429385028},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
					"Documents - individualBiometrics"), error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestWithDocumentsEmptyDocValue() throws JsonParseException, JsonMappingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any())).thenReturn(null);
		Object request = mapper.readValue(
				"{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":795429385028},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"\"}]}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
					"Documents - individualBiometrics"), error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestWithDocumentsInvalidIdentityJsonValidator()
			throws JsonParseException, JsonMappingException, IOException, JsonValidationProcessingException,
			JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any())).thenReturn(null);
		Object request = mapper.readValue(
				"{\"identity\":{},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateRequestWithEmptyIdentity() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		Object request = mapper.readValue("{\"identity\":{}}".getBytes(), Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "update");
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateRequestWithNullRequest() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", null, errors, "create");
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateRequestWithDocumentsInvalidIdentity() throws JsonParseException, JsonMappingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ObjectMapper mockMapper = mock(ObjectMapper.class);
		when(mockMapper.writeValueAsBytes(Mockito.any()))
				.thenThrow(new UnrecognizedPropertyException(null, "", null, null, "", null));
		ReflectionTestUtils.setField(validator, "mapper", mockMapper);
		ReflectionTestUtils.invokeMethod(validator, "validateDocuments",
				mapper.readValue(
						"{\"identity\":{},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
								.getBytes(),
						Map.class),
				errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidateRequestWithDocumentsDuplicateDoc() throws JsonParseException, JsonMappingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ReflectionTestUtils.invokeMethod(validator, "validateDocuments", mapper.readValue(
				"{\"identity\":{\"individualBiometrics\": {\"format\": \"cbeff\", \"version\": 1.0,\"fileReference\": \"le monde est grand et petit\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}, {\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				Map.class), errors);
		assertTrue(errors.hasErrors());
	}

	@Test(expected = IdRepoAppException.class)
	public void testconvertToMap() throws Throwable {
		try {
			ReflectionTestUtils.invokeMethod(validator, "convertToMap", "1234");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testValidateRequestConfigServerConnectionException() throws JsonParseException, JsonMappingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any()))
				.thenThrow(new ConfigServerConnectionException("errorCode", "errorMessage"));
		Object request = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.JSON_SCHEMA_RETRIEVAL_FAILED.getErrorCode(), error.getCode());
			assertEquals(IdRepoErrorConstants.JSON_SCHEMA_RETRIEVAL_FAILED.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestUnidentifiedJsonException() throws JsonParseException, JsonMappingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any()))
				.thenThrow(new UnidentifiedJsonException("errorCode", "errorMessage"));
		Object request = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
					"identity - /identity"), error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequestWithoutIdentity() throws JsonParseException, JsonMappingException, IOException,
			JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		when(jsonValidator.validateJson(Mockito.any()))
				.thenThrow(new NullJsonSchemaException("errorCode", "errorMessage"));
		Object request = mapper.readValue(
				"{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}".getBytes(),
				Object.class);
		ReflectionTestUtils.invokeMethod(validator, "validateRequest", request, errors, "create");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "identity"),
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
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "requesttime"),
					error.getDefaultMessage());
			assertEquals("requesttime", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateReqTimeFutureReqTime() {
		ReflectionTestUtils.invokeMethod(validator, "validateReqTime",
				DateUtils.parseToLocalDateTime("9999-12-31T15:28:28.610Z"), errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "requesttime"),
					error.getDefaultMessage());
			assertEquals("requesttime", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateCreate() throws JsonParseException, JsonMappingException, JsonProcessingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ValidationReport value = new ValidationReport(true, null);
		Mockito.when(jsonValidator.validateJson(Mockito.any())).thenReturn(value);
		Mockito.when(ridValidator.validateId(Mockito.any())).thenReturn(true);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		request.setVersion("v1");
		Object obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);

		RequestDTO req = new RequestDTO();
		req.setRegistrationId("1234");
		req.setStatus("ACTIVATED");
		req.setIdentity(obj);
		request.setRequest(req);
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		validator.validate(request, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidateUpdate() throws JsonParseException, JsonMappingException, JsonProcessingException,
			IOException, JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ValidationReport value = new ValidationReport(true, null);
		Mockito.when(jsonValidator.validateJson(Mockito.any())).thenReturn(value);
		Mockito.when(ridValidator.validateId(Mockito.any())).thenReturn(true);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		request.setVersion("v1");
		Object obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);

		RequestDTO req = new RequestDTO();
		req.setRegistrationId("1234");
		req.setStatus("ACTIVATED");
		req.setIdentity(obj);
		request.setRequest(req);
		request.setRequesttime(DateUtils.getUTCCurrentDateTime());
		validator.validate(request, errors);
		assertFalse(errors.hasErrors());
	}
	
	@Test
	public void testValidateIdNullId() {
		ReflectionTestUtils.invokeMethod(validator, "validateId", null, errors, "read");
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
		ReflectionTestUtils.invokeMethod(validator, "validateId", "abc", errors, "read");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "id"),
					error.getDefaultMessage());
			assertEquals("id", ((FieldError) error).getField());
		});
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testInvalidUin() throws IdRepoAppException {
		when(uinValidator.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		validator.validateUin("1234");
	}
	

	/**
	 * Test retrieve identity null id.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testValidateNullId() throws IdRepoAppException {
		when(uinValidator.validateId(null)).thenThrow(new InvalidIDException(null, null));
		validator.validateUin(null);
	}
	

}
