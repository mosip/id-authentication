package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.UserDetailDAOImpl;
import io.mosip.registration.dto.UserDetailDto;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.repositories.UserBiometricRepository;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.repositories.UserPwdRepository;
import io.mosip.registration.repositories.UserRoleRepository;

public class UserDetailDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private UserDetailDAOImpl userDetailDAOImpl;

	@Mock
	private UserDetailRepository userDetailRepository;

	@Mock
	private UserBiometricRepository userBiometricRepository;
	

	/** The userDetail repository. */
	@Mock
	private UserPwdRepository userPwdRepository;

	/** The userDetail repository. */
	@Mock
	private UserRoleRepository userRoleRepository;

	@Test
	public void getUserDetailSuccessTest() {

		UserDetail userDetail = new UserDetail();
		userDetail.setName("Sravya");
		List<UserDetail> registrationUserDetailList = new ArrayList<UserDetail>();
		registrationUserDetailList.add(userDetail);

		Mockito.when(userDetailRepository.findByIdIgnoreCaseAndIsActiveTrue("mosip"))
				.thenReturn(registrationUserDetailList);
		assertTrue(!registrationUserDetailList.isEmpty());
		assertNotNull(userDetailDAOImpl.getUserDetail("mosip"));
	}

	@Test
	public void getUserDetailFailureTest() {

		UserDetail userDetail = new UserDetail();
		List<UserDetail> registrationUserDetailList = new ArrayList<UserDetail>();
		registrationUserDetailList.add(userDetail);

		Mockito.when(userDetailRepository.findByIdIgnoreCaseAndIsActiveTrue("mosip"))
				.thenReturn(registrationUserDetailList);
		assertFalse(registrationUserDetailList.isEmpty());
		assertNotNull(userDetailDAOImpl.getUserDetail("mosip"));
	}

	@Test
	public void testUpdateLoginParams() {
		UserDetail userDetail = new UserDetail();
		userDetail.setId("mosip");
		userDetail.setUnsuccessfulLoginCount(0);
		userDetail.setUserlockTillDtimes(new Timestamp(new Date().getTime()));
		Mockito.when(userDetailRepository.save(userDetail)).thenReturn(userDetail);
		userDetailDAOImpl.updateLoginParams(userDetail);
	}

	@Test
	public void getAllActiveUsersTest() {
		List<UserBiometric> bioList = new ArrayList<>();
		Mockito.when(userBiometricRepository.findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(Mockito.anyString()))
				.thenReturn(bioList);
		assertEquals(bioList, userDetailDAOImpl.getAllActiveUsers("leftThumb"));
	}

	@Test
	public void getUserSpecificFingerprintDetailsTest() {

		List<UserBiometric> bioList = new ArrayList<>();
		Mockito.when(userBiometricRepository
				.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(Mockito.anyString(),
						Mockito.anyString()))
				.thenReturn(bioList);
		assertEquals(bioList, userDetailDAOImpl.getUserSpecificBioDetails("abcd", "Fingerprint"));

	}

	@Ignore
	@SuppressWarnings("unchecked")
	@Test
	public void userDetlsDao() {
		UserDetailResponseDto userDetailsResponse = new UserDetailResponseDto();
		List<UserDetailDto> userDetails = new ArrayList<>();

		UserDetailDto user = new UserDetailDto();
		user.setUserName("110011");
		user.setUserPassword("test".getBytes());
		user.setRoles(Arrays.asList("SUPERADMIN"));
		user.setMobile("9894589435");
		user.setLangCode("eng");
		UserDetailDto user1 = new UserDetailDto();
		user1.setUserName("110011");
		user1.setUserPassword("test".getBytes());
		user1.setRoles(Arrays.asList("SUPERADMIN"));
		user1.setMobile("9894589435");
		user1.setLangCode("eng");
		userDetails.add(user);
		userDetails.add(user1);
		userDetailsResponse.setUserDetails(userDetails);
		Mockito.when(userDetailRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
		Mockito.when(userPwdRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
		Mockito.when(userRoleRepository.saveAll(Mockito.anyCollection())).thenReturn(new ArrayList<>());
		userDetailDAOImpl.save(userDetailsResponse);
	}
	
	@Test
	public void getUserSpecificBioDetailTest() {
		
		UserBiometric userBiometric = new UserBiometric();
	
		Mockito.when(userBiometricRepository.findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeAndUserBiometricIdBioAttributeCodeIgnoreCase("mosip","bio","sub")).thenReturn(userBiometric);
		assertEquals(userBiometric, userDetailDAOImpl.getUserSpecificBioDetail("mosip","bio","sub"));
	}

}
