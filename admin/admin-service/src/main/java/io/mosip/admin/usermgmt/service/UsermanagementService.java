package io.mosip.admin.usermgmt.service;

import io.mosip.admin.usermgmt.dto.RidVerificationRequestDto;
import io.mosip.admin.usermgmt.dto.RidVerificationResponseDto;
import io.mosip.admin.usermgmt.dto.UserPasswordRequestDto;
import io.mosip.admin.usermgmt.dto.UserPasswordResponseDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationRequestDto;
import io.mosip.admin.usermgmt.dto.UserRegistrationResponseDto;

public interface UsermanagementService {

	UserRegistrationResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto);
	RidVerificationResponseDto ridVerification(RidVerificationRequestDto ridVerificationRequestDto);
	UserPasswordResponseDto addPassword(UserPasswordRequestDto userPasswordRequestDto);
}
