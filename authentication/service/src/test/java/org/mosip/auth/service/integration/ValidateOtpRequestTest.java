package org.mosip.auth.service.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mosip.auth.core.dto.indauth.PinDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.auth.service.integration.dto.OtpGeneratorResponseDto;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(value= {"classpath:rest-services.properties", "classpath:log.properties"})
public class ValidateOtpRequestTest {
	
	@InjectMocks
	RestHelper restHelper;
	
	@Autowired
	MockMvc mvc;
	
	@Autowired
	Environment env;

	private RestRequestFactory restfactory;

	@InjectMocks
	OTPManager otpManager;

	@InjectMocks
	PinDTO pindto;

	private OTPValidateResponseDTO otpvalidateresponsedto;
	
	@Before
	public void before() {
		restfactory = new RestRequestFactory();
		ReflectionTestUtils.setField(restfactory, "env", env);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);

		ReflectionTestUtils.invokeMethod(restfactory, "initializeLogger", mosipRollingFileAppender);
		
		ReflectionTestUtils.invokeMethod(otpManager, "initializeLogger", mosipRollingFileAppender);

		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restfactory);
		ReflectionTestUtils.invokeMethod(restHelper, "initializeLogger", mosipRollingFileAppender);
	}

	/**
	 * To Configure the HTTP resources to Validate OTP
	 * 
	 */

	@BeforeClass
	public static void beforeClass() {
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(
						Mono.just(new OTPValidateResponseDTO("True", "OTP Validation Successful")),
						OTPValidateResponseDTO.class));
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer.create(8085).start(adapter).installShutdownHook();

		RouterFunction<?> functionSuccess1 = RouterFunctions.route(RequestPredicates.GET("/otpmanager/otps"),
				request -> {
					OtpGeneratorResponseDto data = new OtpGeneratorResponseDto();
					data.setOtp("123456");
					return ServerResponse.status(HttpStatus.OK).body(Mono.just(data), OtpGeneratorResponseDto.class);
				});
		HttpHandler httpHandler1 = RouterFunctions.toHttpHandler(functionSuccess1);
		ReactorHttpHandlerAdapter adapter1 = new ReactorHttpHandlerAdapter(httpHandler1);
		HttpServer.create(8086).start(adapter1).installShutdownHook();
		System.err.println("started server");
	}

	/**
	 * Test OTP Validation with key and OTP on Core-kernal
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void Test() throws IdAuthenticationBusinessException {
		assertEquals(true, otpManager.validateOtp("12345", "23232"));
	}
}