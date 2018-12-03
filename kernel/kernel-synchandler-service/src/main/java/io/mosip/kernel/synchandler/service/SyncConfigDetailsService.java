package io.mosip.kernel.synchandler.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface SyncConfigDetailsService {

	public JsonNode getEnrolmentClientConfigDetails();
	public JsonNode  getAdminConfigDetails(String regId);
}
