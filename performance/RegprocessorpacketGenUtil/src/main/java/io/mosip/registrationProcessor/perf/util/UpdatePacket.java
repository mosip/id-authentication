package io.mosip.registrationProcessor.perf.util;

import java.util.Map;

import org.apache.log4j.Logger;

import io.restassured.response.Response;

/**
 * This class is used for testing the Sync API
 * 
 * @author Sayeri Mishra
 *
 */

public class UpdatePacket {

	protected static Logger logger = Logger.getLogger(UpdatePacket.class);

	private final String encrypterURL = "/v1/cryptomanager/encrypt";
	RegProcApiRequests apiRequests = new RegProcApiRequests();

	public Long getUINByRegId(String regId, String validToken) {
		Response idRepoResponse = getIDRepoResponse(regId, validToken);
		Long uin = null;
		Map<String, Object> identity = null;
		Map<String, Map<String, Object>> idRepoResponseBody = idRepoResponse.jsonPath().get("response");
		for (Map.Entry<String, Map<String, Object>> entry : idRepoResponseBody.entrySet()) {
			if (entry.getKey().matches("identity")) {
				identity = entry.getValue();
				for (Map.Entry<String, Object> idObj : identity.entrySet()) {
					if (idObj.getKey().matches("UIN")) {
						uin = (Long) idObj.getValue();
						logger.info("UIN : " + uin);
					}
				}
			}
		}
		return uin;
	}

	private Response getIDRepoResponse(String regId, String validToken) {
		String idRepoUrl = "/idrepository/v1/identity/rid/" + regId + "?type=all";
		Response idRepoResponse = apiRequests.regProcGetIdRepo(idRepoUrl, validToken);
		return idRepoResponse;
	}
}
