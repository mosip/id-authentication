package org.mosip.auth.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.dto.indauth.OtpGeneratorResponseDto;
import org.mosip.auth.core.dto.indauth.PinDTO;
import org.mosip.auth.core.dto.indauth.PinType;
import org.mosip.auth.core.spi.indauth.service.OTPAuthService;
import org.mosip.auth.service.dao.UinRepository;
import org.mosip.auth.service.entity.UinEntity;
import org.mosip.auth.service.integration.OTPManager;
import org.mosip.auth.service.integration.dto.OTPValidateResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class OTPAuthServiceTest {

	@Autowired
	OTPAuthServiceImpl otpauthserviceimpl;

	@Autowired
	OTPManager otpmanager;

	private PinDTO pindto = new PinDTO();

	@Mock
	private UinRepository repository;

	@Autowired
	private OTPAuthService otpauthservice;

	UinEntity uinentity = new UinEntity();

	@BeforeClass
	public static void beforeClass() {
		RouterFunction<?> functionSuccess = RouterFunctions.route(RequestPredicates.POST("/otpmanager/otps"),
				request -> ServerResponse.status(HttpStatus.OK).body(
						Mono.just(new OTPValidateResponseDTO("True", "OTP Validation Successful")),
						OTPValidateResponseDTO.class));
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(functionSuccess);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer.create(8083).start(adapter).installShutdownHook();

		RouterFunction<?> functionSuccess1 = RouterFunctions.route(RequestPredicates.GET("/otpmanager/otps"),
				request -> {
					OtpGeneratorResponseDto data = new OtpGeneratorResponseDto();
					data.setOtp("123456");
					return ServerResponse.status(HttpStatus.OK).body(Mono.just(data), OtpGeneratorResponseDto.class);
				});
		HttpHandler httpHandler1 = RouterFunctions.toHttpHandler(functionSuccess1);
		ReactorHttpHandlerAdapter adapter1 = new ReactorHttpHandlerAdapter(httpHandler1);
		HttpServer.create(8082).start(adapter1).installShutdownHook();
		System.err.println("started server");
	}

	@AfterClass
	public static void afterClass() {
		HttpResources.reset();
	}

	@Test
	public void TestEmpty() {
		String otpval = "";
		boolean value = otpauthserviceimpl.isEmpty(otpval);
		assertEquals(true, value);
	}

	@Test
	public void TestEmtpyString() {
		String otpval = " ";
		boolean value = otpauthserviceimpl.isEmpty(otpval);
		assertEquals(true, value);
	}

	@Test
	public void TestNull() {
		String otpval = null;
		boolean value = otpauthserviceimpl.isEmpty(otpval);
		assertEquals(true, value);
	}

	@Test
	public void TestisNotEmptyorNull() {
		String otpval = "1211212";
		assertFalse(otpauthserviceimpl.isEmpty(otpval));
	}

	@Test
	public void TestvalidOtpValidation() {
		pindto = new PinDTO();
		pindto.setPinValue("12345");
		pindto.setPinType(PinType.OTP);
		String Uin = "123456789";
		String id = "12345";
		UinEntity uinentity = new UinEntity();
		uinentity.setId(id);
		uinentity.setUin(Uin);
		Mockito.when(repository.findByUin(Mockito.anyString())).thenReturn(uinentity);
		UinEntity uinEntity = repository.findByUin(Uin);
		assertEquals("12345", uinEntity.getId());
	}

//	@Test
//	public void ValidOtpRequest() throws IdAuthenticationBusinessException {
//		pindto = new PinDTO();
//		pindto.setPinType(PinType.OTP);
//		pindto.setPinValue("12345");
//		String UIN = "1234567890";
//		boolean value = otpauthservice.validateOtp(pindto, UIN);
//		assertEquals(true, value);
//	}

//	@Test
//	public void InValidOtpRequest() throws IdAuthenticationBusinessException {
//		pindto = null;
//		String UIN = null;
//		boolean value = otpauthservice.validateOtp(pindto, UIN);
//		assertEquals(false, value);
//	}

//	@Test
//	public void TestValidateOtp() {
//		pindto = new PinDTO();
//		PinType pintype = null;
//		pindto.setPinType(pintype);
//		pindto.setPinValue(null);
//		String UIN = "";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test
//	public void TestValidateOTP_PindtoNull() {
//		pindto = null;
//		String UIN = "";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test
//	public void TestValidateOtp_ValidPinDto() {
//		pindto = new PinDTO();
//		PinType pintype = PinType.OTP;
//		pindto.setPinType(pintype);
//		pindto.setPinValue("12345");
//		String UIN = "";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test
//	public void TestValidateOtp_pindtonull() {
//		pindto = null;
//		String UIN = "123456789";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test
//	public void TestValidateOtp_ValidUIN() {
//		pindto = new PinDTO();
//		String UIN = "123456789";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}
//
//	@Test
//	public void TestValidateOtp_InValidPintype() {
//		pindto = new PinDTO();
//		pindto.setPinType(PinType.OTP);
//		pindto.setPinValue("");
//		String UIN = "123456789";
//		assertFalse(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test
//	public void TestValidateOtp_ValidArgs() {
//		pindto = new PinDTO();
//		pindto.setPinType(PinType.OTP);
//		pindto.setPinValue("12345");
//		String UIN = "123456789";
//		assertTrue(otpauthserviceimpl.validateOtpArgs(pindto, UIN));
//	}

//	@Test // TODO throw invalid refid
//	public void TestGetOtpKey_UINNull() {
//		String UIN = null;
//		String value = otpauthserviceimpl.fetchOtpKey(UIN);
//	}

//	@Test // TODO throw invalid refid
//	public void TestGetOtpKey_UINisEmpty() {
//		String UIN = "";
//		String value = otpauthserviceimpl.fetchOtpKey(UIN);
//	}

//	@Test
//	public void TestValidFetchOtpKey() {
//		String UIN = "1234567890";
//		String Uin = "123456789";
//		String id = "12345";
//		UinEntity uinentity = new UinEntity();
//		uinentity.setId(id);
//		uinentity.setUin(Uin);
//		Mockito.when(repository.findByUin(Mockito.anyString())).thenReturn(uinentity);
//		String value = otpauthserviceimpl.fetchOtpKey(UIN);
//		System.out.println(value);
//	}

//	@Test
//	public void TestGetOtpKey_RefidNull() {
//		String refid = null;
//		Optional<String> value = otpauthserviceimpl.getOtpKey(refid);
////		assertNull(value.get());
//	}
//
//	@Test
//	public void TestGetOtpKey_Refidvalid() {
//		String refid = "";
//		Optional<String> value = otpauthserviceimpl.getOtpKey(refid);
//	}

}
