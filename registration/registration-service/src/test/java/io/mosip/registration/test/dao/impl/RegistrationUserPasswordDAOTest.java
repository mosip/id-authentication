package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegistrationUserPasswordDAOImpl;
import io.mosip.registration.entity.RegistrationUserPassword;
import io.mosip.registration.repositories.RegistrationUserPasswordRepository;

public class RegistrationUserPasswordDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private RegistrationUserPasswordDAOImpl registrationUserPassworDAOImpl;

	@Mock
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
	
	@Test
	public void validateUserPasswordSuccessTest() {

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();
		registrationUserPassword.setPwd("mosip");
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);
		assertFalse(registrationUserPasswordList.isEmpty());
		String userData = registrationUserPasswordList.get(0).getPwd();
		assertNotNull(userData);
		assertEquals("mosip", userData);
		assertTrue(registrationUserPassworDAOImpl.getPassword("mosip", "mosip"));
	}

	@Test
	public void validateUserPasswordTest() {

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();
		registrationUserPassword.setPwd(null);
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);
		assertFalse(registrationUserPasswordList.isEmpty());
		String userData = registrationUserPasswordList.get(0).getPwd();
		assertNull(userData);
		assertEquals(null, userData);
		assertFalse(registrationUserPassworDAOImpl.getPassword("mosip", null));
	}

	@Test
	public void validateUserPasswordFailureTest() {

		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();

		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordIdUsrIdAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(registrationUserPasswordList);

		assertTrue(registrationUserPasswordList.isEmpty());
		assertFalse(registrationUserPassworDAOImpl.getPassword("mosip", "mosip"));
	}

}
