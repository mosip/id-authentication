package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegistrationScreenAuthorizationDAOImpl;
import io.mosip.registration.entity.RegistrationScreenAuthorization;
import io.mosip.registration.entity.RegistrationScreenAuthorizationId;
import io.mosip.registration.repositories.RegistrationScreenAuthorizationRepository;

public class RegistrationScreenAuthorizationDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private RegistrationScreenAuthorizationDAOImpl registrationScreenAuthorizationDAOImpl;

	@Mock
	private RegistrationScreenAuthorizationRepository registrationScreenAuthorizationRepository;

	@Test
	public void getScreenAuthorizationDetailsTest() {

		RegistrationScreenAuthorization registrationScreenAuthorization = new RegistrationScreenAuthorization();
		RegistrationScreenAuthorizationId registrationScreenAuthorizationId = new RegistrationScreenAuthorizationId();

		registrationScreenAuthorizationId.setRoleCode("OFFICER");
		registrationScreenAuthorization.setRegistrationScreenAuthorizationId(registrationScreenAuthorizationId);
		registrationScreenAuthorization.setPermitted(true);

		List<RegistrationScreenAuthorization> authorizationList = new ArrayList<>();
		authorizationList.add(registrationScreenAuthorization);
		Mockito.when(registrationScreenAuthorizationRepository
				.findByRegistrationScreenAuthorizationIdRoleCodeAndIsPermittedTrueAndIsActiveTrue("mosip"))
				.thenReturn(authorizationList);
		assertFalse(authorizationList.isEmpty());
		assertNotNull(registrationScreenAuthorizationDAOImpl.getScreenAuthorizationDetails("mosip"));

	}

}
