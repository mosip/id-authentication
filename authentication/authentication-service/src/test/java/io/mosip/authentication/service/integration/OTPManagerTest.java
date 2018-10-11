package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OTPManagerTest.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPManagerTest {

	@Mock
	private RestRequestFactory restRequestFactory;
	@InjectMocks
	AuditRequestFactory auditFactory;

	private OtpGeneratorRequestDto otpGeneratorRequestDto;

	@Autowired
	Environment environment;

	@Mock
	private RestHelper restHelper;

	@InjectMocks
	private OTPManager otpManager;

	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(environment.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(environment.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(environment.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(environment.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(environment.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.invokeMethod(otpManager, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.setField(restRequestFactory, "env", environment);
		ReflectionTestUtils.setField(auditFactory, "env", environment);
	}

	@BeforeClass
	public static void beforeClass() {

		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(Mono.just(new OtpGeneratorResponseDto("89451")),
						OtpGeneratorResponseDto.class));

		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		HttpServer.create(8083).start(adapter);

		System.err.println("started server");

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void testGenerateOTP() throws RestServiceException, IdAuthenticationBusinessException {

		String otpKey = "acbfdhfdh";
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);

		IDDataValidationException e = new IDDataValidationException(
				IdAuthenticationErrorConstants.KERNEL_OTP_GENERATION_REQUEST_FAILED);
		IdAuthenticationBusinessException idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.KERNEL_OTP_GENERATION_REQUEST_FAILED, e);

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenThrow(idAuthenticationBusinessException.getCause());
		otpManager.generateOTP(otpKey);

	}

	@Test
	public void otpTest() throws RestServiceException, IdAuthenticationBusinessException {
		String otpKey = "12345";
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(otpKey);

		OtpGeneratorResponseDto otpGeneratorResponsetDto = new OtpGeneratorResponseDto("870698");
		RestRequestDTO restRequestDTO = getRestRequestDTO();

		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, otpGeneratorRequestDto,
				OtpGeneratorResponseDto.class)).thenReturn(restRequestDTO);

		Mockito.when(restHelper.requestSync(restRequestDTO)).thenReturn(otpGeneratorResponsetDto);
		String response = otpGeneratorResponsetDto.getOtp();
		String expactedOTP = otpManager.generateOTP(otpKey);
		assertEquals(response, expactedOTP);
	}

	// ====================================================================
	// ********************** Helper Method *******************************
	// ====================================================================
	private RestRequestDTO getRestRequestDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		restRequestDTO.setRequestBody(otpGeneratorRequestDto);
		restRequestDTO.setResponseType(OtpGeneratorResponseDto.class);
		restRequestDTO.setTimeout(23);

		return restRequestDTO;
	}

}
