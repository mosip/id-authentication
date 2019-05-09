package io.mosip.idrepository.vid.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
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
import io.mosip.idrepository.vid.dto.RequestDto;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
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
public class VidRequestValidatorTest {

	@InjectMocks
	private VidRequestValidator requestValidator;
	
	@Autowired
	Environment env;
	
	List<String> allowedStatus;
	
	private Map<String, String> id;
	
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
		errors = new BeanPropertyBindingResult(new VidRequestDTO(), "vidRequestDto");
		ReflectionTestUtils.setField(requestValidator, "allowedStatus", allowedStatus);
		ReflectionTestUtils.setField(requestValidator, "id", id);
		ReflectionTestUtils.setField(requestValidator, "env", env);
	}

	@Test
	public void testSupport() {
		assertTrue(requestValidator.supports(VidRequestDTO.class));
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
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), "requestTime"),
					error.getDefaultMessage());
			assertEquals("requestTime", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateReqTimeFutureReqTime() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateReqTime",
				DateUtils.parseToLocalDateTime("9999-12-31T15:28:28.610Z"), errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "requestTime"),
					error.getDefaultMessage());
			assertEquals("requestTime", ((FieldError) error).getField());
		});
	}
	
	@Test
	public void testValidateVerNullVer() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateVersion", null, errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), "version"),
					error.getDefaultMessage());
			assertEquals("version", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateVerInvalidVer() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateVersion", "1234.a", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "version"),
					error.getDefaultMessage());
			assertEquals("version", ((FieldError) error).getField());
		});
	}
	@Test
	public void testValidateStatus_Invalid_Status() {
		ReflectionTestUtils.invokeMethod(requestValidator, "validateStatus", "ACTIVAT", errors);
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "vidStatus"),
					error.getDefaultMessage());
			assertEquals("request", ((FieldError) error).getField());
		});
	}
	
	@Test
	public void testValidateRequest() {
		VidRequestDTO req=new VidRequestDTO();
		req.setId("mosip.vid.update");
		RequestDto request=new RequestDto();
		request.setVidStatus("ACTIVE");
		req.setRequest(request);
		req.setVersion("v1");
		req.setRequestTime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(env.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		ReflectionTestUtils.invokeMethod(requestValidator, "validate", req,errors);
	}
}
