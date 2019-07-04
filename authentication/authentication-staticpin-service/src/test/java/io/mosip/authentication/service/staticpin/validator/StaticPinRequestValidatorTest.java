package io.mosip.authentication.service.staticpin.validator;

import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.staticpin.dto.PinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.staticpin.service.validator.StaticPinRequestValidator;
import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

/**
 * This class Tests The StaticPinValidator class.
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class StaticPinRequestValidatorTest {

	@InjectMocks
	private StaticPinRequestValidator pinRequestValidator;

	@Autowired
	Environment env;

	@InjectMocks
	IdInfoHelper idinfoHelper;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	PinValidatorImpl pinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(pinRequestValidator, "env", env);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);

	}

	@Test
	public void testSupportTrue() {
		assertTrue(pinRequestValidator.supports(StaticPinRequestDTO.class));
	}

	@Test
	public void testStaticPinValidator() {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Mockito.when(pinValidator.validatePin(Mockito.any())).thenReturn(true);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testStaticPinValidator_pinValuenull() {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = null;
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Mockito.when(pinValidator.validatePin(Mockito.any())).thenThrow(
				new InvalidPinException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage()));
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testStaticPinValidator_pinValueEmpty() {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Mockito.when(pinValidator.validatePin(Mockito.any())).thenThrow(
				new InvalidPinException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage()));
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testStaticPinValidator_pinValueInvalid() {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setIndividualIdType(IdType.VID.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "test656";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		Mockito.when(pinValidator.validatePin(Mockito.any())).thenThrow(
				new InvalidPinException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage()));
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testStaticPinValidator_Vid() {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		staticPinRequestDTO.setIndividualIdType(IdType.VID.getType());
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "test656";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Mockito.when(pinValidator.validatePin(Mockito.any())).thenThrow(
				new InvalidPinException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage()));
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testStaticPinValidator_nullObject() {
		StaticPinRequestDTO staticPinRequestDTO = null;
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}

	@Test
	public void TestInvalidUinVidValue() {
		String vid = "5371843613598206";
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		staticPinRequestDTO.setIndividualId(vid);
		staticPinRequestDTO.setIndividualIdType("invalid");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "test656";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
