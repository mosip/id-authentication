package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.PreRegistrationDataSyncDAOImpl;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;

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
		Assert.assertSame(preRegistrationList, preRegistrationDAOImpl.save(preRegistrationList));
	}
	
	@Test
	public void findByPreRegIdTest() {
		Mockito.when(registrationRepository.findByPreRegId(Mockito.anyString())).thenReturn(preRegistrationList);
		Assert.assertSame(preRegistrationList, preRegistrationDAOImpl.get(Mockito.anyString()));
	}
	
	@Test
	public void fetchRecordsToBeDeletedTest() {
		List<PreRegistrationList> preRegList=new ArrayList<>();
		Mockito.when(registrationRepository.findByAppointmentDateBeforeAndIsDeleted(Mockito.any(), Mockito.any())).thenReturn(preRegList);
		assertEquals(preRegList, preRegistrationDAOImpl.fetchRecordsToBeDeleted(new Date()));
	}
	
	@Test
	public void updateTest() {
		Mockito.when(registrationRepository.update(Mockito.any())).thenReturn(preRegistrationList);
		assertEquals(preRegistrationList, preRegistrationDAOImpl.update(preRegistrationList));
	}
	
	@Test
	public void deleteAllTest() {
		Mockito.doNothing().when(registrationRepository).deleteAll(Mockito.any());
		preRegistrationDAOImpl.deleteAll(Mockito.anyList());
	}


}
