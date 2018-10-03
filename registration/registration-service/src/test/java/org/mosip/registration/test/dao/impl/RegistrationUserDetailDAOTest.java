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
import org.mosip.registration.dao.impl.RegistrationUserDetailDAOImpl;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.mosip.registration.repositories.RegistrationUserDetailRepository;

public class RegistrationUserDetailDAOTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationUserDetailDAOImpl registrationUserDetailDAOImpl;
	
	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	
	@Test
	public void getUserDetailSuccessTest(){
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		registrationUserDetail.setCntrId("000567");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		
		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString())).thenReturn(registrationUserDetailList);
			
		registrationUserDetailDAOImpl.getUserDetail(Mockito.anyString());
	}
	
	@Test
	public void getUserDetailFailureTest(){
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);
		
		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue(Mockito.anyString())).thenReturn(registrationUserDetailList);
			
		registrationUserDetailDAOImpl.getUserDetail(Mockito.anyString());
	}
	

}
