package io.mosip.kernel.syncdata.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.constant.UserDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.RegistrationCenterUserDto;
import io.mosip.kernel.syncdata.dto.SyncUserDetailDto;
import io.mosip.kernel.syncdata.dto.UserDetailMapDto;
import io.mosip.kernel.syncdata.dto.response.RegistrationCenterUserResponseDto;
import io.mosip.kernel.syncdata.dto.response.UserDetailResponseDto;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.service.RegistrationCenterUserService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;
import io.mosip.kernel.syncdata.utils.MapperUtils;

/**
 * This class will fetch all user details from the LDAP server through
 * auth-service
 * 
 * @author Srinivasan
 * @author Megha Tanga
 * @since 1.0.0
 */
@Service
public class SyncUserDetailsServiceImpl implements SyncUserDetailsService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	RegistrationCenterUserService registrationCenterUserService;

	@Value("${mosip.auth.user-detail-url:http://localhost:8092/ldapmanager/userdetails}")
	private String authUrl;

	/**
	 * 
	 */
	@Override
	public SyncUserDetailDto getAllUserDetail(String regId) {
		UserDetailResponseDto data = null;
		SyncUserDetailDto syncUserDetailDto = null;
		ResponseEntity<UserDetailResponseDto> response = null;
		RegistrationCenterUserResponseDto registrationCenterResponseDto = registrationCenterUserService
				.getUsersBasedOnRegistrationCenterId(regId);
		List<RegistrationCenterUserDto> registrationCenterUserDtos = registrationCenterResponseDto
				.getRegistrationCenterUsers();
		List<String> userIds = registrationCenterUserDtos.stream().map(RegistrationCenterUserDto::getUserId)
				.collect(Collectors.toList());

		try {
			response = restTemplate.postForEntity(authUrl, userIds, UserDetailResponseDto.class);
			if (response.getStatusCode().is2xxSuccessful())
				data = response.getBody();

			List<UserDetailMapDto> userDetails = MapperUtils.mapUserDetailsToUserDetailMap(data.getUserDetails());
			System.out.println("----data---" + data);
			syncUserDetailDto = new SyncUserDetailDto(userDetails);
			return syncUserDetailDto;

		} catch (RestClientException e) {
			e.printStackTrace();
			throw new DataNotFoundException(UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorCode(),
					UserDetailsErrorCode.USER_DETAILS_FETCH_EXCEPTION.getErrorMessage(), e);
		}

	}

}
