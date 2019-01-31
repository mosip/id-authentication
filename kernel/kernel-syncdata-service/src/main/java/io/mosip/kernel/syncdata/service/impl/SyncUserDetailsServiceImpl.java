package io.mosip.kernel.syncdata.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
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

	@Value("${mosip.auth.user-detail-url:https://integ.mosip.io/ldapmanager/userdetails/admin}")
	private String authUrl;

	/**
	 * 
	 */
	@Override
	public UserDetailResponseDto getAllUserDetail(String regId) {
		String data = null;
		ResponseEntity<String> response = null;
		RegistrationCenterUserResponseDto registrationCenterResponseDto = registrationCenterUserService
				.getUsersBasedOnRegistrationCenterId(regId);
		List<RegistrationCenterUserDto> registrationCenterUserDtos = registrationCenterResponseDto
				.getRegistrationCenterUsers();
		List<String> userIds = registrationCenterUserDtos.stream().map(RegistrationCenterUserDto::getUserId)
				.collect(Collectors.toList());
		try {
			response = restTemplate.postForEntity(authUrl, userIds, String.class);
			if (response.getStatusCode().is2xxSuccessful())
				data = response.getBody();
			System.out.println("----data---"+data);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		
		
		return null;

	}

}
