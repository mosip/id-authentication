package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.AppAuthenticationDetails;
import io.mosip.registration.dao.impl.RegistrationAppAuthenticationDAOImpl;
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

		
		List<AppAuthenticationDetails> loginList = new ArrayList<AppAuthenticationDetails>();
		Mockito.when(registrationAppLoginRepository.findByIsActiveTrueAndRegistrationAppAuthenticationMethodIdProcessNameOrderByMethodSeq(Mockito.anyString())).thenReturn(loginList);

		List<String> modes = new ArrayList<>();
		loginList.stream().map(loginMethod -> loginMethod.getregistrationAppAuthenticationMethodId().getLoginMethod()).collect(Collectors.toList());
		
		assertEquals(modes, registrationAppLoginDAOImpl.getModesOfLogin(Mockito.anyString()));
	}

}
