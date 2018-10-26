package io.mosip.authentication.service.impl.otpgen.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

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
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;

/**
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors error;

	@Autowired
	Environment env;
	
	@Mock
	UinValidatorImpl uinValidator;
	
	@Mock
	VidValidatorImpl vidValidator;

	@InjectMocks
	MosipRollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private OTPRequestValidator otpRequestValidator;

	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	@Before
	public void before() {
		ReflectionTestUtils.setField(otpRequestValidator, "env", env);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(otpRequestValidator.supports(OtpRequestDTO.class));
	}

	@Test
	public void testSupportFalse() {
		assertFalse(otpRequestValidator.supports(AuthRequestValidator.class));
	}

	@Test
	public void testValidUin() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setReqTime(new Date());
		OtpRequestDTO.setIdType(IdType.UIN.getType());
		OtpRequestDTO.setId("426789089018");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new MosipInvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIdType(IdType.UIN.getType());
		OtpRequestDTO.setId("234567890123");
		OtpRequestDTO.setReqTime(new Date());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new MosipInvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		OtpRequestDTO.setReqTime(new Date());
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIdType(IdType.VID.getType());
		OtpRequestDTO.setId("5371843613598206");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new MosipInvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIdType(IdType.VID.getType());
		OtpRequestDTO.setId("5371843613598211");
		OtpRequestDTO.setReqTime(new Date());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidIdType() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIdType("abcd");
		OtpRequestDTO.setReqTime(new Date());
		OtpRequestDTO.setId("5371843613598211");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTimestamp() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIdType("abcd");
		OtpRequestDTO.setReqTime(new Date("1/1/2017"));
		OtpRequestDTO.setId("5371843613598211");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidVer() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setIdType("D");
		otpRequestDTO.setReqTime(new Date());
		otpRequestDTO.setId("5371843613598211");
		otpRequestDTO.setVer("1.12");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidMuaCode() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setIdType("D");
		otpRequestDTO.setReqTime(new Date());
		otpRequestDTO.setId("5371843613598211");
		otpRequestDTO.setVer("1.1");
		otpRequestDTO.setMuaCode("");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidTxnId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setIdType("D");
		otpRequestDTO.setReqTime(new Date());
		otpRequestDTO.setId("5371843613598211");
		otpRequestDTO.setVer("1.1");
		otpRequestDTO.setTxnID("");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testNullId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setIdType("D");
		otpRequestDTO.setReqTime(new Date());
		otpRequestDTO.setId(null);
		otpRequestDTO.setVer("1.1");
		otpRequestValidator.validate(otpRequestDTO, errors);
		errors.getAllErrors().forEach(System.err::println);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testNullIdType() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setIdType(null);
		otpRequestDTO.setReqTime(new Date());
		otpRequestDTO.setId("5371843613598211");
		otpRequestDTO.setVer("1.1");
		otpRequestValidator.validate(otpRequestDTO, errors);
		errors.getAllErrors().forEach(System.err::println);
		assertTrue(errors.hasErrors());
	}
}
