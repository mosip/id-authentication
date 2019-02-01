package io.mosip.authentication.service.impl.indauth.service.pin;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.OtpMatchingStrategy;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.integration.dto.OTPValidateResponseDTO;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpResources;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OtpMatchingStrategyTest {

	@InjectMocks
	IdInfoHelper idInfoHelper;

	@InjectMocks
	private OTPManager otpManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private RestHelper restHelper;
	
	/** The mapper. */
	@InjectMocks
	private ObjectMapper mapper;

	@Autowired
	Environment environment;

	/** The server. */
	static BlockingNettyContext server;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.GET("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(
						Mono.just(new OTPValidateResponseDTO("success", "OTP Validation Successful")),
						OTPValidateResponseDTO.class));
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		server = HttpServer.create(7895).start(adapter);
		server.installShutdownHook();
		System.err.println("started server");

	}

	@AfterClass
	public static void afterClass() {
		server.shutdown();
		HttpResources.reset();
	}

	@Before
	public void before() {
		ReflectionTestUtils.setField(idInfoHelper, "otpManager", otpManager);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
	}

	@Ignore
	@Test
	public void TestValidOtpwithInvalidOtp() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		ValidateOtpFunction func = idInfoHelper.getValidateOTPFunction();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), func);
		int value = matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test
	public void TestValidOtpMatchingStrategy() throws IdAuthenticationBusinessException {
		MockEnvironment env = new MockEnvironment();
		env.merge(((AbstractEnvironment) environment));
		env.setProperty("otp-validate.rest.uri", "http://localhost:8890/otpmanager/otps");
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		ValidateOtpFunction func = idInfoHelper.getValidateOTPFunction();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), func);
		int value = matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
		assertEquals(100, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidOtpMatchingStrategy() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match("123456", "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidreqInfo() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match(123322, "IDA_asdEEFAER", matchProperties);
		assertEquals(0, value);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInValidEntityInfo() throws IdAuthenticationBusinessException {
		MatchFunction matchFunction = OtpMatchingStrategy.EXACT.getMatchFunction();
		Map<String, Object> matchProperties = new HashMap<>();
		matchProperties.put(ValidateOtpFunction.class.getSimpleName(), "");
		int value = matchFunction.match("123456", 12112, matchProperties);
		assertEquals(0, value);
	}

}
