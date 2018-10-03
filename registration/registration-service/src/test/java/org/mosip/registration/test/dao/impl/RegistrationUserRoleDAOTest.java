package org.mosip.registration.test.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.dao.impl.RegistrationUserRoleDAOImpl;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.mosip.registration.repositories.RegistrationUserRoleRepository;

public class RegistrationUserRoleDAOTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationUserRoleDAOImpl registrationUserRoleDAOImpl;
	
	@Mock
	private RegistrationUserRoleRepository registrationUserRoleRepository;
		
	@Test 
	public void getRolesSuccessTest() {
		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setUsrId(Mockito.anyString());
		registrationUserRole.setRegistrationUserRoleID(registrationUserRoleID);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(registrationUserRoleRepository.findByRegistrationUserRoleID(registrationUserRoleID)).thenReturn(registrationUserRoles);
		List<String> roles = new ArrayList<String>();
		for(int role = 0; role < registrationUserRoles.size(); role++) {
			roles.add(registrationUserRoles.get(role).getRegistrationUserRoleID().getRoleCode());
		}
		
		Assert.assertEquals(roles, registrationUserRoleDAOImpl.getRoles(Mockito.anyString()));
	}

	@Test 
	public void getRolesFailureTest() {
		
		RegistrationUserRole registrationUserRole = new RegistrationUserRole();
		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRole.setRegistrationUserRoleID(registrationUserRoleID);
		List<RegistrationUserRole> registrationUserRoles = new ArrayList<RegistrationUserRole>();
		registrationUserRoles.add(registrationUserRole);
		Mockito.when(registrationUserRoleRepository.findByRegistrationUserRoleID(registrationUserRoleID)).thenReturn(registrationUserRoles);
		List<String> roles = new ArrayList<String>();
		
		Assert.assertEquals(roles, registrationUserRoleDAOImpl.getRoles(Mockito.anyString()));
		
	}

}
