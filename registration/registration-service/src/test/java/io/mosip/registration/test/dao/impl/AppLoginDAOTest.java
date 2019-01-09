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
import io.mosip.registration.dao.impl.AppAuthenticationDAOImpl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.AppAuthenticationRepository;

public class AppLoginDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private AppAuthenticationDAOImpl appAuthenticationDAOImpl;

	@Mock
	private AppAuthenticationRepository appAuthenticationRepository;

	@Test
	public void getModesOfLoginSuccessTest() throws RegBaseCheckedException {

		
		List<AppAuthenticationDetails> loginList = new ArrayList<AppAuthenticationDetails>();
		Mockito.when(appAuthenticationRepository.findByIsActiveTrueAndAppAuthenticationMethodIdProcessNameAndRoleCodeOrderByMethodSeq(Mockito.anyString(),Mockito.anyString())).thenReturn(loginList);

		List<String> modes = new ArrayList<>();
		loginList.stream().map(loginMethod -> loginMethod.getAppAuthenticationMethodId().getLoginMethod()).collect(Collectors.toList());
		
		assertEquals(modes, appAuthenticationDAOImpl.getModesOfLogin(Mockito.anyString(), Mockito.anySet()));
	}

}
