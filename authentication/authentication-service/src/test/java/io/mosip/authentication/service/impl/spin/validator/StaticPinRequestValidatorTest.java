package io.mosip.authentication.service.impl.spin.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.spinstore.PinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.otpgen.validator.OTPRequestValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
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
	
	@InjectMocks
	DateHelper dateHelper;

	@Mock
	UinValidatorImpl uinValidator;

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
	public void testSupportFalse() {
		assertFalse(pinRequestValidator.supports(OTPRequestValidator.class));
	}
	
	@Test
	public void testStaticPinValidator() {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
	
	@Test
	public void testStaticPinValidator_pinValuenull() {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = null;
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
	@Test
	public void testStaticPinValidator_pinValueEmpty() {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
	@Test
	public void testStaticPinValidator_pinValueInvalid() {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "test656";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
	
	@Test
	public void testStaticPinValidator_Vid() {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String vid = "5371843613598206";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setVid(vid);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "test656";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
	@Test
	public void testStaticPinValidator_nullObject() {
		StaticPinRequestDTO staticPinRequestDTO=null;
		Errors errors = new BeanPropertyBindingResult(staticPinRequestDTO, "staticPinRequestDTO");
		pinRequestValidator.validate(staticPinRequestDTO, errors);
	}
}
