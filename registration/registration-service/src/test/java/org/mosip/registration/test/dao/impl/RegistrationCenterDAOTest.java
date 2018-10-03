package org.mosip.registration.test.dao.impl;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.dao.impl.RegistrationCenterDAOImpl;
import org.mosip.registration.entity.RegistrationCenter;
import org.mosip.registration.repositories.RegistrationCenterRepository;

public class RegistrationCenterDAOTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationCenterDAOImpl registrationCenterDAOImpl;
	
	@Mock
	private RegistrationCenterRepository registrationCenterRepository;
	
	@Test
	public void getCenterNameSuccessTest() {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		registrationCenter.setName("Registration");
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		
		Mockito.when(registrationCenterRepository.findById(Mockito.anyString())).thenReturn(registrationCenterList);
	
		registrationCenterDAOImpl.getCenterName(Mockito.anyString());
		
	}

	@Test
	public void getCenterNameFailureTest() {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		
		Mockito.when(registrationCenterRepository.findById(Mockito.anyString())).thenReturn(registrationCenterList);
		
		registrationCenterDAOImpl.getCenterName(Mockito.anyString());
		
	}
	
	@Test
	public void getRegistrationCenterDetailsSuccessTest() {
		RegistrationCenter registrationCenter = new RegistrationCenter();
		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findById(Mockito.anyString())).thenReturn(registrationCenterList);
		
		registrationCenterDAOImpl.getRegistrationCenterDetails(Mockito.anyString());
	}


}
