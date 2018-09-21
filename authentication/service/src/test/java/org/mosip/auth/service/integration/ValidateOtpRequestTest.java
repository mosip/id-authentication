/*package org.mosip.auth.service.integration;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.exception.RestServiceException;
import org.mosip.auth.core.factory.RestRequestFactory;
import org.mosip.auth.core.util.RestUtil;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.mosip.auth.service.integration.dto.OtpValidateRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.HttpResources;
import reactor.ipc.netty.http.server.HttpServer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidateOtpRequestTest {
	@Mock
	private RestRequestFactory restfactory;

	@Autowired
	OTPManager otpManager;

	OTPValidateResponseDTO t;
	private OTPValidateResponseDTO otpvalidateresponsedto;

	@BeforeClass
	public static void beforeClass() {
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(
						Mono.just(new OTPValidateResponseDTO("True", "OTP Validation Successful")),
						OTPValidateResponseDTO.class));
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer.create(8083).start(adapter).installShutdownHook();
		System.err.println("started server");
	}

	@AfterClass
	public static void afterClass() {
		HttpResources.reset();
	}

	@Test
	public void TestValidOTPValidate() throws IdValidationFailedException, RestServiceException {
		OtpValidateRequestDTO otpValidateRequestDTO = new OtpValidateRequestDTO();
		otpValidateRequestDTO.setKey("12345");
		otpValidateRequestDTO.setOtp("12323");
		RestRequestDTO requestdto = new RestRequestDTO();
		requestdto.setUri("http://localhost:8083/otpmanager/otps");
		requestdto.setHttpMethod(HttpMethod.POST);
		requestdto.setRequestBody(otpValidateRequestDTO);
		requestdto.setResponseType(OTPValidateResponseDTO.class);
		Mockito.when(restfactory.buildRequest(Mockito.any(RestServicesConstants.class), Mockito.any(), Mockito.any()))
				.thenReturn(requestdto);
		OTPValidateResponseDTO response = RestUtil.requestSync(requestdto);
		assertEquals("True", response.getStatus());
	}

	@Test
	public void TestInvalidOTPValidate() {
//		OTPValidateResponseDTO validresponsedto=new OTPValidateResponseDTO();
//		Mockito.when(RestUtil.requestSync(Mockito.any())).thenReturn(validresponsedto);
//		boolean value = otpManager.validateOtp("", "");
//		System.out.println(value);

	}
}
*/