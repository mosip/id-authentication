package io.mosip.registration.test.util.advice;

import static org.mockito.Mockito.times;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.SecurityContext;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.UserRoleDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.util.advice.AuthenticationAdvice;
import io.mosip.registration.util.advice.PreAuthorizeUserId;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionContext.class, SecurityContext.class })
public class AuthenticationAdviceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private AuthenticationAdvice authenticationAdvice;

	@Mock
	private LoginService loginService;

	@Mock
	private PreAuthorizeUserId preAuthorizeUserId;

	@Mock
	private SecurityContext securityContext;

	@Before
	public void addAuthZTokens() throws Exception {
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.when(SessionContext.securityContext()).thenReturn(securityContext);
		Map<String, Object> value = new HashMap<>();
		LoginUserDTO loginUserDTO = new LoginUserDTO();
		loginUserDTO.setUserId("user");
		value.put(RegistrationConstants.USER_DTO, loginUserDTO);
		PowerMockito.when(securityContext.getUserId()).thenReturn("user");
	}

	@Test
	public void canAuthorizeUserIdAndRole() throws Exception {
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(true);

		UserDTO userDTO = new UserDTO();
		userDTO.setIsActive(true);
		Set<UserRoleDTO> roleDTOs = new LinkedHashSet<>();
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleCode("OFFICER");
		roleDTOs.add(userRoleDTO);
		userDTO.setUserRole(roleDTOs);

		Mockito.when(loginService.getUserDetail("user")).thenReturn(userDTO);
		Mockito.when(preAuthorizeUserId.roles()).thenReturn(new String[] { "OFFICER" });

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);

		Mockito.verify(loginService, times(1)).getUserDetail("user");
		Mockito.verify(preAuthorizeUserId, times(2)).roles();
	}

	@Test(expected = RegBaseCheckedException.class)
	public void canAuthorizeUserIdThrowsExceptionForDiffRole() throws Exception {
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(true);
		
		UserDTO userDTO = new UserDTO();
		userDTO.setIsActive(true);
		Set<UserRoleDTO> roleDTOs = new LinkedHashSet<>();
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleCode("ADMIN");
		roleDTOs.add(userRoleDTO);
		userDTO.setUserRole(roleDTOs);

		Mockito.when(loginService.getUserDetail("user")).thenReturn(userDTO);
		Mockito.when(preAuthorizeUserId.roles()).thenReturn(new String[] { "OFFICER" });

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void canAuthorizeUserIdThrowsExceptionIfSCNA() throws Exception {
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(false);

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);
	}
}
