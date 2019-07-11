package io.mosip.authentication.common.service.builder;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;

public class AuthTransactionBuilderTest {
	
	@Autowired
	Environment env;
	
	@Mock
	IdInfoFetcher idInfoFetcher;
	
	AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
	
	@Test
	public void withOtpRequestTest() {
		assertNotNull(authTransactionBuilder.withOtpRequest(new OtpRequestDTO()));
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void buildTest() throws IdAuthenticationBusinessException {
		ReflectionTestUtils.setField(authTransactionBuilder, "otpRequestDTO", new OtpRequestDTO());
		authTransactionBuilder.build(env);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void buildTest2() throws IdAuthenticationBusinessException {
		authTransactionBuilder.build(env);
	}
	
	@Test
	public void testToString() {
		assertNotNull(authTransactionBuilder.toString());
	}

}
