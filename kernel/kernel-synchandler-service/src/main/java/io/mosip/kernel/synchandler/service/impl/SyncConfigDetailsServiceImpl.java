package io.mosip.kernel.synchandler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;

import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;

public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {

	@Override
	public JsonNode getEnrolmentClientConfigDetails() {
		getEnrolmentConfigDetails();
		return null;
	}

	private void getEnrolmentConfigDetails() {
		
				
		
	}

	@Override
	public JsonNode getAdminConfigDetails() {
		
		return null;
	}

}
