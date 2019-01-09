package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.OTPManager;

/**
 * Test class for OTPServiceImpl.
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
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

	@Test
	public void TestOtpisNullorEmpty() throws IdAuthenticationBusinessException {
		String generateOtp = otpServiceImpl.generateOtp(null);
		assertNull(generateOtp);
	}

	@Test
	public void TestOtpisEmptyvalue() throws IdAuthenticationBusinessException {
		String generateOtp = otpServiceImpl.generateOtp("");
		assertNull(generateOtp);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpisNull() throws IdAuthenticationBusinessException {
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn(null);
		otpServiceImpl.generateOtp("123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestOtpisEmpty() throws IdAuthenticationBusinessException {
		Mockito.when(otpManager.generateOTP(Mockito.anyString())).thenReturn("");
		otpServiceImpl.generateOtp("123456");
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
	public void testGenerateOtpExpactedNullOTP() throws IdAuthenticationBusinessException {

		String otpKey = null;

		String otp = null;

		Mockito.when(otpServiceImpl.generateOtp(otpKey)).thenReturn(otp);
		String expactedOtp = otpServiceImpl.generateOtp(otpKey);
		assertEquals(otp, expactedOtp);
	}
}
