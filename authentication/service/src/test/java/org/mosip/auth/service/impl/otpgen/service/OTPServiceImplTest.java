package org.mosip.auth.service.impl.otpgen.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.integration.OTPManager;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for OTPServiceImpl.
 *
 * @author Rakesh Roshan
 */
//@RunWith(MockitoJUnitRunner.class)
@RunWith(SpringRunner.class)
@TestPropertySource(value = { "classpath:log.properties" })
public class OTPServiceImplTest {

	@Mock
	private OTPManager otpManager;
	@Mock
	private RestRequestFactory restRequestFactory;
	@Mock
	private AuditRequestFactory auditRequestFactory;
	@Mock
	private RestHelper restHelper;
	@Autowired
	Environment env;

	@InjectMocks
	private OTPServiceImpl otpServiceImpl;
	
	@Mock
	private OTPServiceImpl otpServiceImplmock;

	
	@Before
	public void before() {
		// otpRequestDto = getOtpRequestDTO();
		// otpResponseDTO = getOtpResponseDTO();

		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.invokeMethod(otpServiceImpl, "initializeLogger", mosipRollingFileAppender);
	}

	
	@Test
	public void testGenerateOtp() throws IdAuthenticationBusinessException {
		String otpKey = "12345";
		String otp = "806373";

		Mockito.when(otpManager.generateOTP(otpKey)).thenReturn(otp);
		Mockito.when(otpServiceImpl.generateOtp(otpKey)).thenReturn(otp);
		String expactedOtp = otpServiceImpl.generateOtp(otpKey);
		assertEquals(otp, expactedOtp);
	}

	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOtpExpactedException() throws IdAuthenticationBusinessException {
		IdAuthenticationBusinessException e = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		String otpKey1 = "12345";
		String otpKey2 = "12345";
		String otp = null;

		Mockito.when(otpManager.generateOTP(otpKey1)).thenReturn(otp);
		Mockito.when(otpServiceImpl.generateOtp(otpKey1)).thenThrow(e);
		String expactedOtp = otpServiceImpl.generateOtp(otpKey1);
	}
	
	@Test
	public void testGenerateOtpExpactedNullOTP() throws IdAuthenticationBusinessException{
	
		String otpKey = null;
		
		String otp = null;

		Mockito.when(otpServiceImpl.generateOtp(otpKey)).thenReturn(otp);
		String expactedOtp = otpServiceImpl.generateOtp(otpKey);
		assertEquals(otp, expactedOtp);
	}
}


