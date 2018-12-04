package io.mosip.kernel.synchandler.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;

@Service
public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {

	RestTemplate restTemplate = null;

	@Override
	public JsonNode getEnrolmentClientConfigDetails() {
		

		Object jsonObject= getConfigDetailsResponse(
				"http://104.211.212.28:51000/*/default/DEV_SPRINT6_SYNC_HANDLER/global-config.json");
		
		System.out.println(jsonObject);
		return null;
		
	}

	@Override
	public JsonNode getAdminConfigDetails(String regId) {

		Object jsonObject= getConfigDetailsResponse(
				"http://104.211.212.28:51000/*/default/DEV_SPRINT6_SYNC_HANDLER/registration-centre-config.json");
		System.out.println(jsonObject);
		return null;

	}

	private Object getConfigDetailsResponse(String jsonFileUri) {

		StringBuilder uriBuilder = new StringBuilder().append(jsonFileUri);
		Object result = null;
		try {
			restTemplate = new RestTemplate();
			result = restTemplate.getForObject(uriBuilder.toString(), Object.class);
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
