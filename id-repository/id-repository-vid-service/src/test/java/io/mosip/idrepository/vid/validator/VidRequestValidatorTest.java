package io.mosip.idrepository.vid.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
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

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.core.dto.VidRequestDTO;
import io.mosip.idrepository.core.validator.BaseIdRepoValidator;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ConfigurationProperties("mosip.idrepo.vid")
@ActiveProfiles("test")
public class VidRequestValidatorTest {

	@InjectMocks
	private VidRequestValidator requestValidator;

	@Mock
	private VidValidator<String> vidValidator;
	
	@Mock
	private BaseIdRepoValidator  baseValidator;

	@Mock
	private UinValidator<String> uinValidator;

	@Autowired
	protected Environment env;

	@Mock
	private VidPolicyProvider policyProvider;

	List<String> allowedStatus;
	
	Map<String, String> id;

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	public List<String> getAllowedStatus() {
		return allowedStatus;
	}

	public void setAllowedStatus(List<String> allowedStatus) {
		this.allowedStatus = allowedStatus;
	}

	Errors errors;

	@Before
	public void before() {
		errors = new BeanPropertyBindingResult(new RequestWrapper<VidRequestDTO>(), "vidRequestDto");
		ReflectionTestUtils.setField(requestValidator, "allowedStatus", allowedStatus);
		ReflectionTestUtils.setField(requestValidator, "id", id);
		ReflectionTestUtils.setField(baseValidator, "id", id);
		ReflectionTestUtils.setField(requestValidator, "env", env);
		ReflectionTestUtils.setField(requestValidator, "vidValidator", vidValidator);
		ReflectionTestUtils.setField(requestValidator, "policyProvider", policyProvider);
		ReflectionTestUtils.setField(requestValidator, "uinValidator", uinValidator);
	}

	@Test
	public void testSupport() {
		assertTrue(requestValidator.supports(RequestWrapper.class));
	}

	@Test
	public void testSupport_Invalid() {
		assertFalse(requestValidator.supports(IdRequestDTO.class));
	}

	@Test
	public void testValidateReqTimeNullReqTime() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateReqTime", null, errors);
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
		ReflectionTestUtils.invokeMethod(requestValidator, "validateReqTime",
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
	public void testValidateVerNullVer() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateVersion", null, errors);
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
		ReflectionTestUtils.invokeMethod(requestValidator, "validateVersion", "1234.a", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "version"),
					error.getDefaultMessage());
			assertEquals("version", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateStatus_Invalid_Status() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateStatus", "ACTIVAT", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vidStatus"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateStatus_Null_Status() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateStatus", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "vidStatus"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequest() {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.update");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidateRequest_NullRequest() {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.update");
		req.setRequest(null);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "request"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateVid_Valid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(requestValidator, "validateVid", "2015642902372692");
	}

	@Test
	public void testValidateRequest_validateVidType_Valid() {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.create");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		request.setVidType("Perpetual");
		request.setUin(2953190571L);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		HashSet<String> value = new HashSet<String>();
		value.add("Perpetual");
		value.add("Temporary");
		Mockito.when(policyProvider.getAllVidTypes()).thenReturn(value);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testValidateRequest_validateVidType_InValid() {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.create");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		request.setUin(2953190571L);
		request.setVidType("Temp");
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		HashSet<String> value = new HashSet<String>();
		value.add("Perpetual");
		value.add("Temporary");
		Mockito.when(policyProvider.getAllVidTypes()).thenReturn(value);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req, errors);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vidType"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateRequest_validateVidType_Null() {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.create");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		request.setUin(2953190571L);
		request.setVidType(null);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		HashSet<String> value = new HashSet<String>();
		value.add("Perpetual");
		value.add("Temporary");
		Mockito.when(policyProvider.getAllVidTypes()).thenReturn(value);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req, errors);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "vidType"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}
	
	
	
	@Test
	public void testUinValid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		ReflectionTestUtils.invokeMethod(requestValidator, "validateUin", 123456l, errors);
	}
	
	@Test
	public void testUinInValid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
				String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "UIN")));
		ReflectionTestUtils.invokeMethod(requestValidator, "validateUin", 123456l, errors);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "UIN"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}
}
