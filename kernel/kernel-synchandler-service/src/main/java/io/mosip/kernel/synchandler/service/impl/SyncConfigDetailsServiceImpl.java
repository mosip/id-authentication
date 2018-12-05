package io.mosip.kernel.synchandler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

@Service

public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {

	RestTemplate restTemplate = null;

	@Autowired
	Environment env;

	private String configServerUri = env.getProperty("spring.cloud.config.uri");
	private String configLabel = env.getProperty("spring.cloud.config.label");
	private String configProfile = env.getProperty("spring.profiles.active");
	private String configAppName = env.getProperty("spring.application.name");

	@Value("${registration-center-config.json}")
	private String regCenterfileName;

	@Value("${global-config.json}")
	private String globalConfigFileName;

	@Override
	public JSONObject getEnrolmentClientConfigDetails() {

		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(configServerUri).append(configAppName).append(configLabel).append(configProfile)
				.append(regCenterfileName);

		JSONObject jsonObject = getConfigDetailsResponse(uriBuilder.toString());

		return jsonObject;

	}

	@Override
	public JSONObject getAdminConfigDetails(String regId) {

		JSONObject jsonObject = getConfigDetailsResponse(
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
