package org.mosip.auth.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.dto.indauth.PinDTO;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.auth.service.integration.dto.OtpValidateRequestDTO;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
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

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpResources;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.tcp.BlockingNettyContext;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(value = { "classpath:rest-services.properties", "classpath:log.properties" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidateOtpRequestTest {

	@InjectMocks
	RestHelper restHelper;

	@Autowired
	MockMvc mvc;

	@Autowired
	Environment env;

	@Mock
	RestRequestFactory restfactory;

	@InjectMocks
	OTPManager otpManager;

	@InjectMocks
	PinDTO pindto;

	private OTPValidateResponseDTO otpvalidateresponsedto;

//	static BlockingNettyContext server;

	@Before
	public void before() {
		System.err.println(restHelper);
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

//	/**
//	 * To Configure the HTTP resources to Validate OTP
//	 * 
//	 */
//
//	@BeforeClass
//	public static void beforeClass() {
//		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
//				request -> ServerResponse.status(HttpStatus.OK).body(
//						Mono.just(new OTPValidateResponseDTO("True", "OTP Validation Successful")),
//						OTPValidateResponseDTO.class));
//		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
//		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
//		server = HttpServer.create(8083).start(adapter);
//		server.installShutdownHook();
//		System.err.println("Server Started");
//	}

	/**
	 * Test OTP Validation with key and OTP on Core-kernal
	 * 
	 * @throws IdAuthenticationBusinessException
	 * @throws RestServiceException
	 */

	@Test
	public void Test() throws IdAuthenticationBusinessException, RestServiceException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("true");
		otpValidateResponseDTO.setMessage("OTP Validated Successfully");
		RestHelper helper = Mockito.mock(RestHelper.class);
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class))).thenReturn(otpValidateResponseDTO);
		RestRequestDTO requestDTO = new RestRequestDTO();
		RestRequestFactory restreqfactory = Mockito.mock(RestRequestFactory.class);
		Mockito.when(
				restreqfactory.buildRequest(Mockito.any(RestServicesConstants.class), Mockito.any(), Mockito.any()))
				.thenReturn(requestDTO);
		otpManager.restHelper = helper;
		otpManager.restRequestFactory = restreqfactory;
		assertEquals(true, otpManager.validateOtp("12345", "23232"));
	}

	

	@Test(expected = IdAuthenticationBusinessException.class)
	public void zTest_InvalidvalidateOTP() throws RestServiceException, IdAuthenticationBusinessException {
		RestHelper helper = Mockito.mock(RestHelper.class);
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class)))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED));
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);
		otpManager.validateOtp("2323", "2323");
	}

}