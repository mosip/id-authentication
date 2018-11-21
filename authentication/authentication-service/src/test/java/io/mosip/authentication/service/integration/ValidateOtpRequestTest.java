package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.OTPValidateResponseDTO;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidateOtpRequestTest {

	@InjectMocks
	RestHelper restHelper;

	@Autowired
	MockMvc mvc;

	@Autowired
	Environment env;

	@Mock
	OTPValidateResponseDTO otpvalidateresponsedto;

	@InjectMocks
	RestRequestFactory restfactory;

	@InjectMocks
	OTPManager otpManager;

	@InjectMocks
	PinDTO pindto;

	// static BlockingNettyContext server;

	@Before
	public void before() {
		ReflectionTestUtils.setField(restfactory, "env", env);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restfactory);
	}

	// /**
	// * To Configure the HTTP resources to Validate OTP
	// *
	// */
	//
	// @BeforeClass
	// public static void beforeClass() {
	// RouterFunction<?> functionSuccess =
	// RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
	// request -> ServerResponse.status(HttpStatus.OK).body(
	// Mono.just(new OTPValidateResponseDTO("True", "OTP Validation Successful")),
	// OTPValidateResponseDTO.class));
	// HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
	// ReactorHttpHandlerAdapter adapter = new
	// ReactorHttpHandlerAdapter(httpHandler);
	// server = HttpServer.create(8083).start(adapter);
	// server.installShutdownHook();
	// System.err.println("Server Started");
	// }

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
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restreqfactory);
		ReflectionTestUtils.setField(otpManager, "otpvalidateresponsedto", otpvalidateresponsedto);

		// TODO: for validate OTP as true
		assertEquals(false, otpManager.validateOtp("12345", "23232"));
	}

	@Test
	public void zTest_InvalidvalidateOTP() throws RestServiceException, IdAuthenticationBusinessException {
		RestHelper helper = Mockito.mock(RestHelper.class);
		OTPValidateResponseDTO otpvalidateresponsedto = new OTPValidateResponseDTO();
		otpvalidateresponsedto.setStatus("failure");
		otpvalidateresponsedto.setStatus("OTP_EXPIRED");
		Mockito.when(helper.requestSync(Mockito.any(RestRequestDTO.class))).thenReturn(otpvalidateresponsedto);
		ReflectionTestUtils.setField(otpManager, "restHelper", helper);

		assertFalse(otpManager.validateOtp("2323", "2323"));
	}

}