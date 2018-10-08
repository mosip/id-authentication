package io.mosip.authentication.service.impl.otpgen.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

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
import org.springframework.test.context.TestPropertySource;
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
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

/**
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@TestPropertySource(value = { "classpath:ValidationMessages.properties", "classpath:audit.properties",
		"classpath:rest-services.properties", "classpath:log.properties" })
public class OTPRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@InjectMocks
	MosipRollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private OTPRequestValidator otpRequestValidator;

	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.setField(otpRequestValidator, "env", env);
		ReflectionTestUtils.invokeMethod(otpRequestValidator, "initializeLogger", mosipRollingFileAppender);
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
		OtpRequestDTO.setId("234567890124");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
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
		errors.getFieldErrors().forEach(System.err::println);
		assertTrue(errors.hasErrors());
	}
}
