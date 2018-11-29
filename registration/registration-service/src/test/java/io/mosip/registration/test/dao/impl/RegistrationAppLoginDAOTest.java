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
import io.mosip.registration.dao.impl.RegistrationAppLoginDAOImpl;
import io.mosip.registration.entity.RegistrationAppLoginMethod;
import io.mosip.registration.entity.RegistrationAppLoginMethodId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.RegistrationAppLoginRepository;

public class RegistrationAppLoginDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private RegistrationAppLoginDAOImpl registrationAppLoginDAOImpl;

	@Mock
	private RegistrationAppLoginRepository registrationAppLoginRepository;

	@Test
	public void getModesOfLoginSuccessTest() throws RegBaseCheckedException {

		RegistrationAppLoginMethod registrationAppLoginMethod = new RegistrationAppLoginMethod();
		RegistrationAppLoginMethodId registrationAppLoginMethodId = new RegistrationAppLoginMethodId();
		registrationAppLoginMethodId.setLoginMethod("PWD");
		registrationAppLoginMethod.setMethodSeq(1);
		registrationAppLoginMethod.setRegistrationAppLoginMethodId(registrationAppLoginMethodId);
		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		loginList.add(registrationAppLoginMethod);

		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);

		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(
				p -> modes.put(String.valueOf(p.getMethodSeq()), p.getRegistrationAppLoginMethodId().getLoginMethod()));
		modes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		assertEquals(modes, registrationAppLoginDAOImpl.getModesOfLogin());
	}

	@Test
	public void getModesOfLoginFailureTest() throws RegBaseCheckedException {

		List<RegistrationAppLoginMethod> loginList = new ArrayList<RegistrationAppLoginMethod>();
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueOrderByMethodSeq()).thenReturn(loginList);

		Map<String, Object> modes = new LinkedHashMap<String, Object>();
		loginList.forEach(
				p -> modes.put(String.valueOf(p.getMethodSeq()), p.getRegistrationAppLoginMethodId().getLoginMethod()));
		modes.put(RegistrationConstants.LOGIN_SEQUENCE, RegistrationConstants.PARAM_ONE);
		assertEquals(modes, registrationAppLoginDAOImpl.getModesOfLogin());
	}

}
