package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.OTPValidateResponseDTO;
import io.mosip.authentication.service.integration.dto.OtpGeneratorRequestDto;
import io.mosip.authentication.service.integration.dto.OtpGeneratorResponseDto;
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

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidGenerateKey() throws RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO restRequestDTO = getRestRequestDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_GENERATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any()))
				.thenThrow(new RestServiceException(IdAuthenticationErrorConstants.SERVER_ERROR));
		otpManager.generateOTP("123456");
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceException()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("OTP_EXPIRED");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO));
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}

	@Test
	public void TestOtpAuth()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("success");
		otpValidateResponseDTO.setMessage("VALIDATION_SUCCESSFUL");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpValidateResponseDTO);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
		assertTrue(expactedOTP);
	}
	
	@Test
	public void TestOtpAuthFailure()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failue");
		otpValidateResponseDTO.setMessage("VALIDATION_UNSUCCESSFUL");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenReturn(otpValidateResponseDTO);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
		assertFalse(expactedOTP);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalid()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("VALIDATION_UNSUCCESSFUL");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO));
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidUnknownMessage()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		otpValidateResponseDTO.setStatus("failure");
		otpValidateResponseDTO.setMessage("SOME UNKNOWN MESSAGE");
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO));
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatus()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(otpValidateResponseDTO);
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO));
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithOTPNOTGENERATEDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		String output = "{\"errors\":[{\"errorCode\":\"KER-OTV-005\",\"errorMessage\":\"Validation can't be performed against this key. Generate OTP first.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestRestServiceExceptionwithInvalidWithoutStatusWithSOMEOTHERERRORDError()
			throws RestServiceException, IdAuthenticationBusinessException, JsonProcessingException {
		OTPValidateResponseDTO otpValidateResponseDTO = new OTPValidateResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("Invalid");
		String output = "{\"errors\":[{\"errorCode\":\"KER-SOME-OTHER-001\",\"errorMessage\":\"Some other error.\"}]}";
		RestRequestDTO restRequestDTO = getRestRequestvalidDTO();
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenReturn(restRequestDTO);
		RestServiceException restServiceException = new RestServiceException(
				IdAuthenticationErrorConstants.INVALID_REST_SERVICE, output, otpValidateResponseDTO);
		Mockito.when(restHelper.requestSync(Mockito.any())).thenThrow(restServiceException);
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestDataValidationException() throws IdAuthenticationBusinessException {
		Mockito.when(restRequestFactory.buildRequest(RestServicesConstants.OTP_VALIDATE_SERVICE, null,
				OTPValidateResponseDTO.class)).thenThrow(new IDDataValidationException());
		boolean expactedOTP = otpManager.validateOtp("Test123", "123456");
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

	private RestRequestDTO getRestRequestvalidDTO() {
		RestRequestDTO restRequestDTO = new RestRequestDTO();
		OTPValidateResponseDTO otValidateResponseDTO = new OTPValidateResponseDTO();
		restRequestDTO.setHttpMethod(HttpMethod.POST);
		restRequestDTO.setUri("http://localhost:8083/otpmanager/otps");
		restRequestDTO.setRequestBody(otValidateResponseDTO);
		restRequestDTO.setResponseType(OTPValidateResponseDTO.class);
		restRequestDTO.setTimeout(23);
		return restRequestDTO;
	}

}
