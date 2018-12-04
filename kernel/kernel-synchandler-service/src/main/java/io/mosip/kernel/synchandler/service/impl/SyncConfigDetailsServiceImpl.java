package io.mosip.kernel.synchandler.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

@Service
public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {

	RestTemplate restTemplate = null;

	@Override
	public JSONObject getEnrolmentClientConfigDetails() {
		

		JSONObject jsonObject= getConfigDetailsResponse(
				"http://104.211.212.28:51000/*/default/DEV_SPRINT6_SYNC_HANDLER/global-config.json");
		
		
		return jsonObject;
		
	}

	@Override
	public JSONObject getAdminConfigDetails(String regId) {

		JSONObject jsonObject= getConfigDetailsResponse(
				"http://104.211.212.28:51000/*/default/DEV_SPRINT6_SYNC_HANDLER/registration-centre-config.json");
		
		return jsonObject;

	}

	private JSONObject getConfigDetailsResponse(String jsonFileUri) {

		StringBuilder uriBuilder = new StringBuilder().append(jsonFileUri);
		JSONObject result = null;
		try {
			restTemplate = new RestTemplate();
			result = restTemplate.getForObject(uriBuilder.toString(), JSONObject.class);
		} catch (RestClientException e) {
			// throw appropriate error
			
		}

		return result;
		/*
		 * ObjectMapper mapper = new ObjectMapper(); JsonFactory factory =
		 * mapper.getFactory(); JsonParser parser; JsonNode actualObj = null; try {
		 * parser = factory.createParser(response); actualObj = mapper.readTree(parser);
		 * 
		 * } catch (IOException e) {
		 * 
		 * }
		 * 
		 * return actualObj;
		 */

	}

}
