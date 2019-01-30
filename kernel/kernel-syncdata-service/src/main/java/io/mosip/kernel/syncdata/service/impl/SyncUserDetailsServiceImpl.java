package io.mosip.kernel.syncdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.UserDetailDto;
import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;

/**
 * This class will fetch all userdetails from the LDAP server through
 * auth-service
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Service
public class SyncUserDetailsServiceImpl implements SyncUserDetailsService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	RegistrationCenterUserService registrationCenterUserService;

	/**
	 * 
	 */
	@Override
	public UserDetailResponseDto getAllUserDetail(String regId) {

		RegistrationCenterUserResponseDto registrationCenterResponseDto = registrationCenterUserService
				.getUsersBasedOnRegistrationCenterId(regId);
		List<RegistrationCenterUserDto> registrationCenterUserDtos = registrationCenterResponseDto
				.getRegistrationCenterUsers();
		String uri="https://integ.mosip.io/ldapmanager/userdetails/admin";
		
		    UserDetailDto userDetailDtos=restTemplate.getForObject(uri, UserDetailDto.class);
		    System.out.println(userDetailDtos.getMobile());
		return null;

	}

}
