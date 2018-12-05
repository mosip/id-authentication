package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.impl.RegistrationAppAuthenticationDAOImpl;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethod;
import io.mosip.registration.entity.RegistrationAppAuthenticationMethodId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationAppAuthenticationRepository;

public class RegistrationAppLoginDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private RegistrationAppAuthenticationDAOImpl registrationAppLoginDAOImpl;

	@Mock
	private RegistrationAppAuthenticationRepository registrationAppLoginRepository;

	@Test
	public void getModesOfLoginSuccessTest() throws RegBaseCheckedException {

		RegistrationAppAuthenticationMethod registrationAppLoginMethod = new RegistrationAppAuthenticationMethod();
		RegistrationAppAuthenticationMethodId registrationAppLoginMethodId = new RegistrationAppAuthenticationMethodId();
		registrationAppLoginMethodId.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setregistrationAppAuthenticationMethodId(registrationAppLoginMethodId);
		List<RegistrationAppAuthenticationMethod> loginList = new ArrayList<RegistrationAppAuthenticationMethod>();
		loginList.add(registrationAppLoginMethod);

		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq(Mockito.anyString())).thenReturn(loginList);

		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(
				p -> modes.put(String.valueOf(p.getMethodSeq()), p.getregistrationAppAuthenticationMethodId().getLoginMethod()));
		modes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		assertEquals(modes, registrationAppLoginDAOImpl.getModesOfLogin(Mockito.anyString()));
	}

	@Test
	public void getModesOfLoginFailureTest() throws RegBaseCheckedException {

		List<RegistrationAppAuthenticationMethod> loginList = new ArrayList<RegistrationAppAuthenticationMethod>();
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq(Mockito.anyString())).thenReturn(loginList);

		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(
				p -> modes.put(String.valueOf(p.getMethodSeq()), p.getregistrationAppAuthenticationMethodId().getLoginMethod()));
		modes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		assertEquals(modes, registrationAppLoginDAOImpl.getModesOfLogin(Mockito.anyString()));
	}

}
