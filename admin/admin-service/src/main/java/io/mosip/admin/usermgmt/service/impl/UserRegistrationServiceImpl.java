package io.mosip.admin.usermgmt.service.impl;

import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;
import io.mosip.admin.usermgmt.service.UserRegistrationService;

public class UserRegistrationServiceImpl implements UserRegistrationService {

	@Override
	public UserRegistrationResponseDto register(UserRegistrationRequestDto request) {
		UserRegistrationResponseDto dto= new UserRegistrationResponseDto("SUCCESS");
		return dto;
	}

}
