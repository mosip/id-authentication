package io.mosip.kernel.otpmanager.test.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.otpmanager.exception.CryptoFailureException;
import io.mosip.kernel.otpmanager.util.OtpProvider;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OtpProviderTest {

	@Autowired
	private OtpProvider provider;

	@Test(expected = CryptoFailureException.class)
	public void getSigningExceptionTest() {
		provider.computeOtp("98989898999", 6, "nsjkbdcdj");
	}

}
