package io.mosip.kernel.syncdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.constant.SyncConfigDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.response.RolesResponseDto;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.service.SyncRolesService;

/**
 * 
 * @author Srinivasan
 * 
 */
@Service
public class SyncRolesServiceImpl implements SyncRolesService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUrl;
	
	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authServiceName;
	
	@Override
	public RolesResponseDto getAllRoles() {
		RolesResponseDto rolesDtos = null;
		try {
			// URI should be called from properties file
			StringBuilder uriBuilder=new StringBuilder();
			uriBuilder.append(authBaseUrl).append(authServiceName);
			System.out.println(uriBuilder.toString());
			rolesDtos = restTemplate.getForObject(uriBuilder.toString(),
					RolesResponseDto.class);
		} catch (RestClientException ex) {
			throw new SyncDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorMessage());
		}

		return rolesDtos;

	}

}
