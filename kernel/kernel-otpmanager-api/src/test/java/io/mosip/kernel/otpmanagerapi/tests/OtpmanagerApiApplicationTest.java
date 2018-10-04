package io.mosip.kernel.otpmanagerapi.tests;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import io.mosip.kernel.core.spi.otpmanager.OtpGenerator;

import io.mosip.kernel.otpmanagerapi.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanagerapi.exception.MosipResourceNotFoundException;
import io.mosip.kernel.otpmanagerapi.generator.OtpGeneratorImpl;

public class OtpmanagerApiApplicationTest {

	@Test
	public void generateOtpFromApiTest() {
		OtpGenerator otpGenerator = new OtpGeneratorImpl();
		assertThat(otpGenerator.generateOtp(), isA(String.class));
	}

	@Test(expected = MosipResourceNotFoundException.class)
	public void exceptionTest() {
		OtpGeneratorImpl otp = mock(OtpGeneratorImpl.class);
		doThrow(new MosipResourceNotFoundException(OtpErrorConstants.OTP_GEN_RESOURCE_NOT_FOUND.getErrorCode(),
				OtpErrorConstants.OTP_GEN_RESOURCE_NOT_FOUND.getErrorMessage())).when(otp).generateOtp();
		otp.generateOtp();
	}
}
