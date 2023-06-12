package io.mosip.authentication.authtypelockfilter.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.*;
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
		status.setAuthSubType("");
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status,authtypeStatusList);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusDemoLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		IdentityDTO identityDto = new IdentityDTO();
		identityDto.setDob("1998/01/01");
		request.setDemographics(identityDto);
		authRequestDTO.setRequest(request);
		
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.DEMO.getType());
		status.setLocked(true);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status,authtypeStatusList);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusBioLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		List<BioIdentityInfoDTO> list = new ArrayList<>();
		BioIdentityInfoDTO bioId = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setBioType("Finger");
		data.setBioSubType("UNKNOWN");
		bioId.setData(data);
		list.add(bioId);
		request.setBiometrics(list);
		authRequestDTO.setRequest(request);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.BIO.getType());
		status.setAuthSubType(BioAuthType.FACE_IMG.getType());
		status.setLocked(true);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		Map<String, String> value = new HashMap<>();
		value.put("Face", "--face-image--");
		Mockito.when(idInfoFetcher.getIdentityRequestInfo(BioMatchType.FACE, authRequestDTO.getRequest(), null))
				.thenReturn(value);

		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status,authtypeStatusList);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void TestvalidateAuthTypeStatusOTPLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("123");
		authRequestDTO.setRequest(request);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.OTP.getType());
		status.setAuthSubType("email");
		status.setLocked(false);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		AuthtypeStatus status1 = new AuthtypeStatus();
		status1.setAuthType(MatchType.Category.OTP.getType());
		status1.setAuthSubType("phone");
		status1.setLocked(true);
		authtypeStatusList.add(status1);
		Mockito.when(authTypeStatusService.fetchAuthtypeStatus(Mockito.anyString())).thenReturn(authtypeStatusList);
		try {
			ReflectionTestUtils.invokeMethod(authTypeLockFilterImpl, "validateAuthTypeStatus", authRequestDTO, status,
					authtypeStatusList);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestvalidateAuthTypeStatusPinLocked() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("123");
		authRequestDTO.setRequest(request);
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.SPIN.getType());
		status.setLocked(true);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		Map<String, Object> properties = new HashMap<>();
		properties.put("TOKEN", "1122");
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		IdAuthenticationBusinessException exception = new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
				String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
						MatchType.Category.SPIN.getType()));
		Mockito.doThrow(exception).when(authTypeStatusService).fetchAuthtypeStatus("1122");
		authTypeLockFilterImpl.validate(authRequestDTO, identityData, properties);
	}

	@Test(expected = IdAuthenticationFilterException.class)
	public void validateExceptionTest1() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("123");
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		properties.put("TOKEN", "1122");
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.SPIN.getType());
		status.setAuthSubType("phone");
		status.setLocked(true);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		Mockito.when(authTypeStatusService.fetchAuthtypeStatus("1122")).thenReturn(authtypeStatusList);
		authTypeLockFilterImpl.validate(authRequestDTO, identityData, properties);
	}

	@Test(expected = IdAuthenticationFilterException.class)
	public void validateExceptionTest2() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("123");
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		properties.put("TOKEN", "1122");
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.SPIN.getType());
		status.setLocked(true);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		authtypeStatusList.add(status);
		System.out.println("1= "+authtypeStatusList);
		IdAuthenticationBusinessException exception = new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
				String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
						MatchType.Category.SPIN.getType()));
		Mockito.doThrow(exception).when(authTypeStatusService).fetchAuthtypeStatus("1122");
//		Mockito.when(authTypeStatusService.fetchAuthtypeStatus("1122")).thenReturn(authtypeStatusList);
		authTypeLockFilterImpl.validate(authRequestDTO, identityData, properties);
	}

	@Test
	public void validateTest() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setStaticPin("123");
		authRequestDTO.setRequest(request);
		Map<String, List<IdentityInfoDTO>> identityData = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		properties.put("TOKEN", "1122");
		AuthtypeStatus status = new AuthtypeStatus();
		status.setAuthType(MatchType.Category.SPIN.getType());
		status.setLocked(false);
		List<AuthtypeStatus> authtypeStatusList = new ArrayList<>();
		Mockito.when(authTypeStatusService.fetchAuthtypeStatus("1122")).thenReturn(authtypeStatusList);
		authTypeLockFilterImpl.validate(authRequestDTO, identityData, properties);

		authtypeStatusList.add(status);
		authTypeLockFilterImpl.validate(authRequestDTO, identityData, properties);
	}

}
