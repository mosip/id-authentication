package io.mosip.kernel.syncdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.syncdata.constant.SyncConfigDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
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
	RestTemplate restTemplate;

	@Override
	public RolesResponseDto getAllRoles() {
		RolesResponseDto rolesDtos = null;
		try {
			// URI should be called from properties file
			rolesDtos = restTemplate.getForObject("https://integ.mosip.io/ldapmanager/allroles",
					RolesResponseDto.class);
		} catch (RestClientException ex) {
			throw new SyncDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorMessage());
		}

		return rolesDtos;

	}

}
