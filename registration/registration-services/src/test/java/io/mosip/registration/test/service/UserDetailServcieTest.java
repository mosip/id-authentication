package io.mosip.registration.test.service;

import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.UserDetailDto;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.UserOnboardService;
import io.mosip.registration.service.impl.UserDetailServiceImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserDetailDAO.class })
public class UserDetailServcieTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private UserOnboardService userOnboardService;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@InjectMocks
	private UserDetailServiceImpl userDetailServiceImpl;

	@Mock
	private UserDetailDAO userDetailDAO;

	@Test
	public void userDtls() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(userDetail);
		userDetailServiceImpl.save();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDtlsException() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenThrow(HttpClientErrorException.class);
		userDetailServiceImpl.save();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDtlsException1() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean()))
				.thenThrow(SocketTimeoutException.class);
		userDetailServiceImpl.save();
	}

}
