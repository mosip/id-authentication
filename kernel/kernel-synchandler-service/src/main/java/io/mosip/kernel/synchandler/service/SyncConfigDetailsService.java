package io.mosip.kernel.synchandler.service;

import net.minidev.json.JSONObject;

public interface SyncConfigDetailsService {

	public JSONObject getEnrolmentClientConfigDetails();
	public JSONObject  getAdminConfigDetails(String regId);
}
