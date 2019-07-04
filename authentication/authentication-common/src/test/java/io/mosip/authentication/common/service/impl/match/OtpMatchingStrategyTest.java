package io.mosip.authentication.common.service.impl.match;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.OtpMatchingStrategy;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OtpMatchingStrategyTest {

	@InjectMocks
	IdInfoFetcherImpl idInfoFetcherImpl;

	@InjectMocks
	private OTPManager otpManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@Mock
	private RestHelper restHelper;

	/** The mapper. */
	@InjectMocks
	private ObjectMapper mapper;

	@Autowired
	Environment environment;

	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoFetcherImpl, "otpManager", otpManager);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
	}

	@Test
	public void TestValidOtpwithInvalidOtp() throws IdAuthenticationBusinessException, RestServiceException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		ValidateOtpFunction func = idInfoFetcherImpl.getValidateOTPFunction();
		Map<String, Object> otpResponseDTO = new HashMap<String, Object>();
		otpResponseDTO.put("status", "success");
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpResponseDTO);
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), func);
		int value = matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestValidOtpMatchingStrategy() throws IdAuthenticationBusinessException, RestServiceException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		ValidateOtpFunction func = idInfoFetcherImpl.getValidateOTPFunction();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), func);
		Map<String, Object> otpResponseDTO = new HashMap<String, Object>();
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("status", "success");
		otpResponseDTO.put("response", (Object) valueMap);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpResponseDTO);
		matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidOtpMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInValidreqInfo() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match(123322, "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestInValidEntityInfo() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match("123456", 12112, matchProperties);
		assertEquals(0, value);
	}

}
