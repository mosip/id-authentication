package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegistrationCenterDAOImpl;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationCenterId;
import io.mosip.registration.repositories.RegistrationCenterRepository;

public class RegistrationCenterDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationCenterDAOImpl registrationCenterDAOImpl;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Test
	public void getRegistrationCenterDetailsSuccessTest() {

		RegistrationCenter registrationCenter = new RegistrationCenter();
		RegistrationCenterId registrationCenterId = new RegistrationCenterId();
		registrationCenter.setRegistrationCenterId(registrationCenterId);
		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByRegistrationCenterIdCenterIdAndIsActiveTrue("mosip"))
				.thenReturn(registrationCenterList);
		assertTrue(registrationCenterList.isPresent());
		assertNotNull(registrationCenterDAOImpl.getRegistrationCenterDetails("mosip"));
	}

}
