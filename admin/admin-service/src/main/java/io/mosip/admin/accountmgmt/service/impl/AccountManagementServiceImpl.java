package io.mosip.admin.accountmgmt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.accountmgmt.service.AccountManagementService;

/**
 * The Class AccountManagementServiceImpl.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
public class AccountManagementServiceImpl implements AccountManagementService {

	@Autowired
	RestTemplate restTemplate;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.admin.accountmgmt.service.AccountManagementService#getUserName(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public String getUserName(String userId, String phoneNumber) {

		return null;
	}
	
	//private void 

}
