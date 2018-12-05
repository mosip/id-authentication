package io.mosip.kernel.synchandler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;
/**
 * 
 * @author Srinivasan
 *
 */
@RestController
public class SyncConfigDetailsController {

	/**
	 * Service instance {@link SyncConfigDetailsService}
	 */
	@Autowired
	SyncConfigDetailsService syncConfigDetailsService;
	/**
	 * This API method would fetch all synced global config details from server
	 * @return JSONObject - global config response 
	 */
	@GetMapping(value="/globalconfigs")
	public JSONObject getGlobalConfigDetails() {
		return syncConfigDetailsService.getGlobalConfigDetails();
	}
	/**
	 * This API method would fetch all synced registration center config details from server
	 * @return JSONObject
	 */
	@GetMapping(value="/registrationcenterconfig/{registrationcenterid}")
	public JSONObject getRegistrationCentreConfig(@PathVariable(value="registrationcenterid") String regId) {
		return syncConfigDetailsService.getRegistrationCenterConfigDetails(regId);
	}
	
}
