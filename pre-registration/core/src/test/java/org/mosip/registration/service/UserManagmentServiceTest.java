package org.mosip.registration.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.registration.constants.StatusCodes;
import org.mosip.registration.dao.UserManagementRepository;
import org.mosip.registration.dto.UserDto;
import org.mosip.registration.entity.UserManagmentEntity;
import org.mosip.registration.exceptions.UserNameNotValidException;
import org.mosip.registration.service.impl.UserManagementServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

@SpringBootTest

public class UserManagmentServiceTest {

	@Mock
	private UserManagementRepository userManagementRepository;

	@InjectMocks
	private UserManagementService userManagementService = new UserManagementServiceImpl();
	UserManagmentEntity userManagmentEntity = new UserManagmentEntity(1, "phone", "9988815042");
	UserNameNotValidException exception = new UserNameNotValidException(StatusCodes.USER_ID_INVALID.toString());

	@Test
	public void updateUserSuccessCheck() {
		String userName = "9988815042";
		UserDto userDto = new UserDto();
		userDto.setUserName("9988905333");
		Map<String, StatusCodes> mockResponseMap = new HashMap<>();
		mockResponseMap.put("ok", StatusCodes.USER_UPDATED);
		Mockito.when(userManagementRepository.getUserByUserName(userName)).thenReturn(userManagmentEntity);
		Mockito.when(userManagementRepository.save(ArgumentMatchers.any())).thenReturn(userManagmentEntity);
		Map<String, StatusCodes> responseMap = userManagementService.userUpdation(userName, userDto);
		assertEquals(mockResponseMap, responseMap);

	}

	@Test(expected = UserNameNotValidException.class)
	public void updateUserFaliureCheck() {
		String userName = "9988815042";
		UserDto userDto = new UserDto();
		userDto.setUserName("998890533");
		Mockito.when(userManagementRepository.getUserByUserName(userName)).thenReturn(userManagmentEntity);
		userManagementService.userUpdation(userName, userDto);
	}

	@Test
	public void SaveUserSuccessCheck() {
		String userName = "9988905333";
		UserDto userDto = null;
		Map<String, StatusCodes> mockResponseMap = new HashMap<>();
		mockResponseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
		Mockito.when(userManagementRepository.getUserByUserName(userName)).thenReturn(null);
		Mockito.when(userManagementRepository.save(ArgumentMatchers.any())).thenReturn(userManagmentEntity);
		Map<String, StatusCodes> responseMap  = userManagementService.userUpdation(userName, userDto);
		assertEquals(mockResponseMap, responseMap);
	}

	@Test
	public void ExistingUserSuccessCheck() {
		String userName = "9988905333";
		UserDto userDto = null;
		Map<String, StatusCodes> mockResponseMap = new HashMap<>();
		mockResponseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
		Mockito.when(userManagementRepository.getUserByUserName(userName)).thenReturn(userManagmentEntity);
		Mockito.when(userManagementRepository.save(ArgumentMatchers.any())).thenReturn(userManagmentEntity);
		Map<String, StatusCodes> responseMap  = userManagementService.userUpdation(userName, userDto);
		assertEquals(mockResponseMap, responseMap);

	}

}
