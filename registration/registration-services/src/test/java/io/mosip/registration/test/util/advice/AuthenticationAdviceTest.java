package io.mosip.registration.test.util.advice;

import static org.mockito.Mockito.times;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
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

import com.google.common.collect.Sets;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.context.SessionContext.SecurityContext;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserRole;
import io.mosip.registration.entity.id.UserRoleID;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.util.advice.AuthenticationAdvice;
import io.mosip.registration.util.advice.PreAuthorizeUserId;
import io.mosip.registration.util.restclient.RequestHTTPDTO;

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

		UserDetail userDetail = new UserDetail();
		userDetail.setIsActive(true);
		UserRoleID userRoleID = new UserRoleID();
		userRoleID.setRoleCode("OFFICER");
		UserRole userRole = new UserRole();
		userRole.setUserRoleID(userRoleID);
		userDetail.setUserRole(Sets.newHashSet(userRole));

		Mockito.when(loginService.getUserDetail("user")).thenReturn(userDetail);
		Mockito.when(preAuthorizeUserId.roles()).thenReturn(new String[] { "OFFICER" });

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);

		Mockito.verify(loginService, times(1)).getUserDetail("user");
		Mockito.verify(preAuthorizeUserId, times(2)).roles();
	}

	@Test(expected = RegBaseCheckedException.class)
	public void canAuthorizeUserIdThrowsExceptionForDiffRole() throws Exception {
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(true);

		UserDetail userDetail = new UserDetail();
		userDetail.setIsActive(true);
		UserRoleID userRoleID = new UserRoleID();
		userRoleID.setRoleCode("ADMIN");
		UserRole userRole = new UserRole();
		userRole.setUserRoleID(userRoleID);
		userDetail.setUserRole(Sets.newHashSet(userRole));

		Mockito.when(loginService.getUserDetail("user")).thenReturn(userDetail);
		Mockito.when(preAuthorizeUserId.roles()).thenReturn(new String[] { "OFFICER" });

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void canAuthorizeUserIdThrowsExceptionIfSCNA() throws Exception {
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(false);

		authenticationAdvice.authorizeUserId(preAuthorizeUserId);
	}
}
