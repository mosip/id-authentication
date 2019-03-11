package io.mosip.authentication.service.impl.otpgen.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

import io.mosip.authentication.core.dto.otpgen.ChannelDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPRequestValidatorTest {

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@InjectMocks
	RollingFileAppender idaRollingFileAppender;

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
		OtpRequestDTO.setId("id");
		// OtpRequestDTO.setVer("1.1");
		OtpRequestDTO.setPartnerID("1234567890");
		OtpRequestDTO.setTransactionID("1234567890");
		ZoneOffset offset = ZoneOffset.MAX;
		OtpRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		OtpRequestDTO.setIndividualId("5076204698");
		OtpRequestDTO.setIndividualIdType("D");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setMispLicenseKey("TEst");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setIndividualId("5076204698");
		OtpRequestDTO.setPartnerID("1234567890");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		OtpRequestDTO.setId("id");
		// OtpRequestDTO.setVer("1.1");
		OtpRequestDTO.setPartnerID("1234567890");
		OtpRequestDTO.setTransactionID("1234567890");
		OtpRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setIndividualId("5371843613598206");
		OtpRequestDTO.setIndividualIdType("V");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setMispLicenseKey("test");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("id", "code"));
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setPartnerID("1234567890");
		OtpRequestDTO.setIndividualId("5371843613598211");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTimeout() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setRequestTime(new Date("1/1/2017").toInstant().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		OtpRequestDTO.setIndividualId("5371843613598211");
		OtpRequestDTO.setPartnerID("1234567890");
		OtpRequestDTO.setId("id");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTimeParseError() {
		OtpRequestDTO OtpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO, "OtpRequestDTO");
		OtpRequestDTO.setRequestTime("123-123-45-32");
		OtpRequestDTO.setId("id");
		OtpRequestDTO.setPartnerID("1234567890");
		OtpRequestDTO.setIndividualId("5371843613598211");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		OtpRequestDTO.setOtpChannel(channel);
		OtpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(OtpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Ignore
	@Test
	public void testInvalidVer() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setId("id");
		otpRequestDTO.setPartnerID("1234567890");
		otpRequestDTO.setIndividualId("5371843613598211");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestDTO.setVersion("1.12");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testInvalidTxnId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setId("id");
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setTransactionID("");
		otpRequestDTO.setPartnerID("1234567890");
		otpRequestDTO.setIndividualId("5371843613598211");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		otpRequestDTO.setOtpChannel(channel);
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testNullId() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "OtpRequestDTO");
		otpRequestDTO.setRequestTime(Instant.now().toString());
		otpRequestDTO.setVersion("1.1");
		otpRequestDTO.setId("id");
		otpRequestDTO.setIndividualId("");
		otpRequestDTO.setPartnerID("1234567890");
		ChannelDTO channel = new ChannelDTO();
		channel.setPhone(true);
		otpRequestDTO.setOtpChannel(channel);
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
