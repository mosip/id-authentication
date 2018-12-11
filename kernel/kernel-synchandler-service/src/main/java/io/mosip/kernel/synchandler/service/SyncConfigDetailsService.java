package io.mosip.kernel.synchandler.service;

import net.minidev.json.JSONObject;
/**
 * Configuration Sync service
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public interface SyncConfigDetailsService {
    /**
     *  This service will fetch all Global Configaration details available from server
     * @return JSONObject - global config synced data
     */
	public JSONObject getGlobalConfigDetails();
	
	/**
	 * This service will fetch all Registration center specific config details from server
	 *
	 * @param regId - registration Id
	 * @return JSONObject - registration center config synced data
	 */
	public JSONObject  getRegistrationCenterConfigDetails(String regId);
}
