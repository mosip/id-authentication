
package io.mosip.authentication.service.validator;



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
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

import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.otp.service.validator.OTPRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * The Class OTPRequestValidatorTest.
 *
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPRequestValidatorTest {

	/** The error. */
	@Mock
	Errors error;

	/** The env. */
	@Autowired
	Environment env;

	/** The uin validator. */
	@Mock
	UinValidatorImpl uinValidator;

	/** The vid validator. */
	@Mock
	VidValidatorImpl vidValidator;

	/** The ida rolling file appender. */
	@InjectMocks
	RollingFileAppender idaRollingFileAppender;

	/** The otp request validator. */
	@InjectMocks
	private OTPRequestValidator otpRequestValidator;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(otpRequestValidator, "env", env);
	}

	/**
	 * Test support true.
	 */
	@Test
	public void testSupportTrue() {
		assertTrue(otpRequestValidator.supports(OtpRequestDTO.class));
	}

	/**
	 * Test support false.
	 */
	@Test
	public void testSupportFalse() {
		assertFalse(otpRequestValidator.supports(AuthRequestValidator.class));
	}

	/**
	 * Test valid uin.
	 */
	@Ignore
	@Test
	public void testValidUin() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setTransactionID("1234567890");
		OtpRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				// offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		OtpRequestDTO.setIndividualId("5076204698");
		OtpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test invalid uin.
	 */
	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setIndividualId("5076204698");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		OtpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test valid vid.
	 */
	@Ignore
	@Test
	public void testValidVid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setTransactionID("1234567890");
		OtpRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIndividualId("5371843613598206");
		OtpRequestDTO.setIndividualIdType(IdType.VID.getType());
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test invalid vid.
	 */
	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setIndividualId("5371843613598211");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		OtpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test timeout.
	 */
	@SuppressWarnings("deprecation")

	@Test
	public void testTimeout() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setRequestTime(new Date("1/1/2017").toInstant().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		OtpRequestDTO.setIndividualId("5371843613598211");
		OtpRequestDTO.setId("id");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		OtpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test time parse error.
	 */
	@Test
	public void testTimeParseError() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setRequestTime("123-123-45-32");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setIndividualId("5371843613598211");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		OtpRequestDTO.setOtpChannel(channelList);
		OtpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test invalid ver.
	 */
	@Ignore

	@Test
	public void testInvalidVer() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("5371843613598211");
		otpRequestDTO.setVersion("1.12");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test invalid txn id.
	 */
	@Test
	public void testInvalidTxnId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setId("id");
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setTransactionID("");
		otpRequestDTO.setIndividualId("5371843613598211");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test null id.
	 */
	@Test
	public void testNullId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("");
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("PHONE");
		otpRequestDTO.setOtpChannel(channelList);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test invalid time.
	 */
	@Test
	public void TestInvalidTime() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setTransactionID("TXN0000001");
		otpRequestDTO.setRequestTime("2019-03-15T09:23:50.635");
		otpRequestDTO.setIndividualId("5371843613598211");
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
	}

	/**
	 * Test otp channel.
	 */
	@Ignore
	@Test
	public void TestOtpChannel() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("5076204698");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("mobile");
		channelList.add("email");
		otpRequestDTO.setOtpChannel(channelList);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	/**
	 * Test invalid otp channel type.
	 */
	@Test
	public void TestInvalidOtpChannelType() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("5076204698");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		ArrayList<String> channelList = new ArrayList<String>();
		channelList.add("invalid");
		otpRequestDTO.setOtpChannel(channelList);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	/**
	 * Test otp channelis null.
	 */
	@Test
	public void TestOtpChannelisNull() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("5076204698");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		otpRequestDTO.setOtpChannel(null);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
	}

	/**
	 * Test otp channelis empty.
	 */
	@Test
	public void TestOtpChannelisEmpty() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("5076204698");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		otpRequestDTO.setOtpChannel(new ArrayList<>());
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
	}

	/**
	 * Testparse exception.
	 */
	@Test
	public void TestparseException() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		ReflectionTestUtils.invokeMethod(otpRequestValidator, "validateRequestTimedOut", "test", errors);
		
	}

}
