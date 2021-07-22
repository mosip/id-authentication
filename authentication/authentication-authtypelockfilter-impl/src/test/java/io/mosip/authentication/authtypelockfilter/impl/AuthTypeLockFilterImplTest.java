package io.mosip.authentication.authtypelockfilter.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class AuthTypeLockFilterImplTest {
	
	@InjectMocks
	private AuthTypeLockFilterImpl authTypeLockFilterImpl;
	
	@Mock
	private AuthtypeStatusImpl authTypeStatusService;
	
	
	@Mock
	private IdInfoFetcher idInfoFetcher;
	
	
	@Before
	public void before() {
	}
	
	
	@Test
	public void TestvalidateAuthTypeStatusNotLocked() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthtypeStatus status = new AuthtypeStatus();
		status.setLocked(false);
		ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusDemoLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setDemo(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.DEMO.getType());
		status.setLocked(true);

		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusBioLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setBio(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.BIO.getType());
		status.setAuthSubType(BioAuthType.FACE_IMG.getType());
		status.setLocked(true);

		Map<String, String> value = new HashMap<>();
		value.put("Face", "--face-image--");
		Mockito.when(idInfoFetcher.getIdentityRequestInfo(BioMatchType.FACE, authRequestDTO.getRequest(), null))
				.thenReturn(value);

		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusOTPLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.OTP.getType());
		status.setLocked(true);

		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusPinLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setPin(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.SPIN.getType());
		status.setLocked(true);

		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}


}
