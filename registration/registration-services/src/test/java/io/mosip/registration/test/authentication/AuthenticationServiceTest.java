package io.mosip.registration.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
<<<<<<< HEAD
import io.mosip.registration.service.security.AuthenticationService;
=======
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.UserPasswordDTO;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.security.impl.AuthenticationServiceImpl;
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
import io.mosip.registration.validator.AuthenticationBaseValidator;
import io.mosip.registration.validator.FingerprintValidatorImpl;
import io.mosip.registration.validator.OTPValidatorImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HMACUtils.class, CryptoUtil.class})
public class AuthenticationServiceTest {

	@InjectMocks
	private AuthenticationService authenticationService;
	
	@Mock
	FingerprintValidatorImpl fingerprintValidator;

	@Mock
	OTPValidatorImpl otpValidator;
	
	@Mock
	private LoginService loginService;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
<<<<<<< HEAD
=======
	@InjectMocks
	private AuthenticationServiceImpl authenticationServiceImpl;
	
	@Before
	public void initialize() {
		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.mockStatic(CryptoUtil.class);
	}
	
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	@Test
	public void getOtpValidatorTest() {
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(otpValidator);
<<<<<<< HEAD
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(otpValidator.validate(authenticationValidatorDTO)).thenReturn(true);
		assertTrue(authenticationService.authValidator("otp", authenticationValidatorDTO));
=======
		authenticationServiceImpl.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthTokenDTO authTokenDTO =new AuthTokenDTO();
		when(otpValidator.validate("mosip", "12345")).thenReturn(authTokenDTO);
		assertNotNull(authenticationServiceImpl.authValidator("otp", "mosip", "12345"));
>>>>>>> 4483d04c7d451fda25350bad5c0d157b05369082
	}
	
	@Test
	public void getFPValidatorTest() {
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(fingerprintValidator);
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(fingerprintValidator.validate(authenticationValidatorDTO)).thenReturn(true);
		assertTrue(authenticationService.authValidator("Fingerprint", authenticationValidatorDTO));
	}
	
	@Test
	public void getFPValidatorNegativeTest() {
		List<AuthenticationBaseValidator> authenticationBaseValidators=new ArrayList<>();
		authenticationBaseValidators.add(fingerprintValidator);
		authenticationService.setAuthenticationBaseValidator(authenticationBaseValidators);
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		when(fingerprintValidator.validate(authenticationValidatorDTO)).thenReturn(false);
		assertFalse(authenticationService.authValidator("otp", authenticationValidatorDTO));
	}
	
	@Test
	public void validatePasswordTest() {
		UserDTO userDTO = new UserDTO();
		userDTO.setSalt("salt");
		UserPasswordDTO userPassword = new UserPasswordDTO();
		userPassword.setPwd("mosip");
		userDTO.setUserPassword(userPassword);
		
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setPassword("mosip");
		
		Mockito.when(loginService.getUserDetail("mosip")).thenReturn(userDTO);		
		Mockito.when(CryptoUtil.decodeBase64("salt")).thenReturn("salt".getBytes());		
		Mockito.when(HMACUtils.digestAsPlainTextWithSalt("mosip".getBytes(), "salt".getBytes())).thenReturn("mosip");
		
		assertEquals("Username and Password Match", authenticationServiceImpl.validatePassword(authenticationValidatorDTO));
		
	}
	
	@Test
	public void validatePasswordNotMatchTest() {
		UserDTO userDTO = new UserDTO();
		userDTO.setSalt("salt");
		UserPasswordDTO userPassword = new UserPasswordDTO();
		userPassword.setPwd("mosip");
		userDTO.setUserPassword(userPassword);
		
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setPassword("mosip");
		
		Mockito.when(loginService.getUserDetail("mosip")).thenReturn(userDTO);		
		Mockito.when(CryptoUtil.decodeBase64("salt")).thenReturn("salt".getBytes());		
		Mockito.when(HMACUtils.digestAsPlainTextWithSalt("mosip1".getBytes(), "salt".getBytes())).thenReturn("mosip1");
		
		assertEquals("Username and Password Not Match", authenticationServiceImpl.validatePassword(authenticationValidatorDTO));
		
	}
	
	@Test
	public void validatePasswordFailureTest() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("mosip");
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		authenticationValidatorDTO.setUserId("mosip");
		authenticationValidatorDTO.setPassword("mosip");
	
		Mockito.when(loginService.getUserDetail("mosip")).thenReturn(userDTO);			
		assertEquals("Username and Password Not Match", authenticationServiceImpl.validatePassword(authenticationValidatorDTO));
		
	}
	
	@Test
	public void validatePasswordFailure1Test() {		
		AuthenticationValidatorDTO authenticationValidatorDTO=new AuthenticationValidatorDTO();
		
		Mockito.when(loginService.getUserDetail("mosip")).thenReturn(null);			
		assertEquals("Username and Password Not Match", authenticationServiceImpl.validatePassword(authenticationValidatorDTO));
		
	}

}
