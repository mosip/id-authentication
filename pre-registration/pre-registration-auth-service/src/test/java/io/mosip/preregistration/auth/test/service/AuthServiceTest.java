package io.mosip.preregistration.auth.test.service;


import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import io.mosip.preregistration.auth.dto.MainRequestDTO;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.dto.Otp;
import io.mosip.preregistration.auth.dto.OtpUser;
import io.mosip.preregistration.auth.dto.OtpUserDTO;
import io.mosip.preregistration.auth.dto.User;
import io.mosip.preregistration.auth.errorcodes.ErrorCodes;
import io.mosip.preregistration.auth.errorcodes.ErrorMessages;
import io.mosip.preregistration.auth.exceptions.InvalidateTokenException;
import io.mosip.preregistration.auth.exceptions.SendOtpFailedException;
import io.mosip.preregistration.auth.exceptions.UserIdOtpFaliedException;
import io.mosip.preregistration.auth.service.AuthService;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import junit.framework.Assert;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceTest {

	@Mock
	private MainResponseDTO<AuthNResponse> mainResponseDTO;
	@Mock
	private MainRequestDTO<Otp> otpRequest;
	@Mock
	MainRequestDTO<User> userRequest;
	@Mock
	private Otp otp;
	@Mock
	private User user;
	@MockBean
	private AuthCommonUtil authCommonUtil;
	@Mock
	private OtpUser otpUser;
	@Mock
	private OtpUserDTO otpUserDto;
	@Mock
	private ResponseEntity<String> responseEntity;
	@Mock
	private AuthNResponse authNResposne;
	@Autowired
	@InjectMocks
	private AuthService authService;
	private List<String> list;
	@Before
	public void setUp() {
		 list=new ArrayList<>();
		 //responseEntity=new ResponseEntity<AuthNResponse>(authNResposne, HttpStatus.OK);
		 
	}
	@Test
	public void sendOtpTest() throws Exception {
		list.add("mobile");
		Mockito.when(authCommonUtil.validateRequest(otpRequest)).thenReturn(true);
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		assertNotNull(authService.sendOTP(otpRequest));
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void sendOtpTest_Exception() throws Exception {
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateRequest(otpRequest)).thenReturn(true);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode", "errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		authService.sendOTP(otpRequest);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void sendOtpTest_AuthException() throws Exception {
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateRequest(otpRequest)).thenReturn(true);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode","errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		authService.sendOTP(otpRequest);
	}
	@Test
	public void validateWithUserIdOtp() {
		Mockito.when(authCommonUtil.validateRequest(userRequest)).thenReturn(true);
		Mockito.when(userRequest.getRequest()).thenReturn(user);
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(userRequest);
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		assertNotNull(authService.validateWithUserIdOtp(userRequest));
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void validateWithUserIdOtp_Exception()  {
		Mockito.when(authCommonUtil.validateRequest(userRequest)).thenReturn(true);
		Mockito.when(userRequest.getRequest()).thenReturn(user);
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(userRequest);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode","errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authService.validateWithUserIdOtp(userRequest);
	}
	
	@Test
	public void invalidateToken() {
		String authHeader="Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NzQ4MTA3Mzg2IiwibW9iaWxlIjoiOTc0ODEwNzM4NiIsIm1haWwiOiIiLCJuYW1lIjoiOTc0ODEwNzM4NiIsImlzT3RwUmVxdWlyZWQiOnRydWUsImlzT3RwVmVyaWZpZWQiOnRydWUsImlhdCI6MTU1MjM4NDk1NCwiZXhwIjoxNTUyMzkwOTU0fQ.burEVnDRF4YVyRGMdx0vYP2DkZbiCKnUdl-7YDlBgcy3u40W5iE9_P8q9kdrlt2xjk4NuXnjPkb7uaFbzYcHog; Max-Age=6000000; Expires=Mon, 20-May-2019 20:42:34 GMT; Path=/; Secure; HttpOnly";
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("Success");
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.when(authCommonUtil.requestBodyExchange(Mockito.any())).thenReturn(authNResposne);
		assertNotNull(authService.invalidateToken(authHeader));
	}
	
	@Test(expected=InvalidateTokenException.class)
	public void invalidateToken_Exception() {
		String authHeader="Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NzQ4MTA3Mzg2IiwibW9iaWxlIjoiOTc0ODEwNzM4NiIsIm1haWwiOiIiLCJuYW1lIjoiOTc0ODEwNzM4NiIsImlzT3RwUmVxdWlyZWQiOnRydWUsImlzT3RwVmVyaWZpZWQiOnRydWUsImlhdCI6MTU1MjM4NDk1NCwiZXhwIjoxNTUyMzkwOTU0fQ.burEVnDRF4YVyRGMdx0vYP2DkZbiCKnUdl-7YDlBgcy3u40W5iE9_P8q9kdrlt2xjk4NuXnjPkb7uaFbzYcHog; Max-Age=6000000; Expires=Mon, 20-May-2019 20:42:34 GMT; Path=/; Secure; HttpOnly";
		Mockito.doThrow(new RestClientException("Rest Client exception Occurred")).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("Success");
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		assertNotNull(authService.invalidateToken(authHeader));
	}
}
