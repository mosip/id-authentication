package io.mosip.registration.test.dao.impl;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.dao.impl.PreRegistrationDataSyncDAOImpl;
import io.mosip.registration.dao.impl.RegistrationAppAuthenticationDAOImpl;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;
import io.mosip.registration.repositories.RegistrationAppAuthenticationRepository;

public class PreRegistrationDAOImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private PreRegistrationDataSyncDAOImpl preRegistrationDAOImpl;

	@Mock
	private PreRegistrationDataSyncRepository registrationRepository;

	@Mock
	private PreRegistrationList preRegistrationList;

	@Test
	public void savetest() {
		
		Mockito.when(registrationRepository.save(preRegistrationList)).thenReturn(preRegistrationList);
		Assert.assertSame(preRegistrationList, preRegistrationDAOImpl.savePreRegistration(preRegistrationList));
	}
	
	@Test
	public void findByPreRegIdTest() {
		Mockito.when(registrationRepository.findByPreRegId(Mockito.anyString())).thenReturn(preRegistrationList);
		Assert.assertSame(preRegistrationList, preRegistrationDAOImpl.getPreRegistration(Mockito.anyString()));
	}
	

}
