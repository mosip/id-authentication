package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.RegistrationUserDetailDAOImpl;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.repositories.RegistrationUserDetailRepository;
import io.mosip.registration.repositories.UserBiometricRepository;

public class RegistrationUserDetailDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationUserDetailDAOImpl registrationUserDetailDAOImpl;

	@Mock
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	
	@Mock
	private UserBiometricRepository userBiometricRepository;

	@Test
	public void getUserDetailSuccessTest() {

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setName("Sravya");
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue("mosip"))
				.thenReturn(registrationUserDetailList);
		assertTrue(!registrationUserDetailList.isEmpty());
		assertNotNull(registrationUserDetailDAOImpl.getUserDetail("mosip"));
	}

	@Test
	public void getUserDetailFailureTest() {

		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		List<RegistrationUserDetail> registrationUserDetailList = new ArrayList<RegistrationUserDetail>();
		registrationUserDetailList.add(registrationUserDetail);

		Mockito.when(registrationUserDetailRepository.findByIdAndIsActiveTrue("mosip"))
				.thenReturn(registrationUserDetailList);
		assertFalse(registrationUserDetailList.isEmpty());
		assertNotNull(registrationUserDetailDAOImpl.getUserDetail("mosip"));
	}
	
	@Test
	public void testUpdateLoginParams() {
		RegistrationUserDetail registrationUserDetail = new RegistrationUserDetail();
		registrationUserDetail.setId("mosip");
		registrationUserDetail.setUnsuccessfulLoginCount(0);
		registrationUserDetail.setUserlockTillDtimes(new Timestamp(new Date().getTime()));
		Mockito.when(registrationUserDetailRepository.save(registrationUserDetail)).thenReturn(registrationUserDetail);
		registrationUserDetailDAOImpl.updateLoginParams(registrationUserDetail);
	}
	
	@Test
	public void getAllActiveUsersTest() {
		List<UserBiometric> bioList=new ArrayList<>();
		Mockito.when(userBiometricRepository.findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(Mockito.anyString())).thenReturn(bioList);
		assertEquals(bioList, registrationUserDetailDAOImpl.getAllActiveUsers("leftThumb"));
	}
	
	@Test
	public void getUserSpecificFingerprintDetailsTest() {

		List<UserBiometric> bioList=new ArrayList<>();
		Mockito.when(userBiometricRepository.findByUserBiometricIdUsrIdAndIsActiveTrue(Mockito.anyString())).thenReturn(bioList);
		assertEquals(bioList, registrationUserDetailDAOImpl.getUserSpecificFingerprintDetails("abcd"));
	
	}

}
