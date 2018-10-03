package org.mosip.registration.test.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.kernel.core.utils.HMACUtil;
import org.mosip.registration.dao.impl.RegistrationUserPasswordDAOImpl;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;

public class RegistrationUserPasswordDAOTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationUserPasswordDAOImpl registrationUserPassworDAOImpl;
	
	@Mock
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
	
	@Test 
	public void validateUserPasswordSuccessTest() {
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		String password = "mosip";
		byte[] bytePassword = password.getBytes();
		String hashPassword = HMACUtil.digestAsPlainText(HMACUtil.generateHash(bytePassword));
		registrationUserPasswordID.setUsrId("mosip");
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();		
		registrationUserPassword.setRegistrationUserPasswordID(registrationUserPasswordID);
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordID(Mockito.anyObject())).thenReturn(registrationUserPasswordList);
		
		registrationUserPassworDAOImpl.getPassword("mosip", "E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262");
	}
	
	@Test 
	public void validateUserPasswordFailureTest() {
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		String password = "mosip";
		byte[] bytePassword = password.getBytes();
		String hashPassword = HMACUtil.digestAsPlainText(HMACUtil.generateHash(bytePassword));
		registrationUserPasswordID.setUsrId("mosip");
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPasswordList = new ArrayList<RegistrationUserPassword>();
		RegistrationUserPassword registrationUserPassword = new RegistrationUserPassword();		
		registrationUserPassword.setRegistrationUserPasswordID(registrationUserPasswordID);
		registrationUserPasswordList.add(registrationUserPassword);
		Mockito.when(registrationUserPasswordRepository.findByRegistrationUserPasswordID(Mockito.anyObject())).thenReturn(registrationUserPasswordList);
		
		registrationUserPassworDAOImpl.getPassword("mosip", "E2E488ECAF91897D71BEAC2589433898414FEEB1408372");
	}
	

}
