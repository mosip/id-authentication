package io.mosip.kernel.synchandler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.synchandler.constant.SyncConfigDetailsErrorCode;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

/**
 * Implementation class
 * 
 * @author Srinivasan
 *
 */
@Service
public class SyncConfigDetailsServiceImpl implements SyncConfigDetailsService {
	@Autowired
	RestTemplate restTemplate;

	/**
	 * Environment instance
	 */
	@Autowired
	Environment env;

	@Value("${mosip.kernel.registration.center.config.file.name}")
	private String regCenterfileName;

	@Value("${mosip.kernel.global.config.file.name}")
	private String globalConfigFileName;

	private String configServerUri = null;
	private String configLabel = null;
	private String configProfile = null;
	private String configAppName = null;

	@Override
	public JSONObject getGlobalConfigDetails() {

		JSONObject jsonObject = getConfigDetailsResponse(globalConfigFileName);

		return jsonObject;

	}

	@Override
	public JSONObject getRegistrationCenterConfigDetails(String regId) {

		JSONObject jsonObject = getConfigDetailsResponse(regCenterfileName);

		return jsonObject;
	}

	private JSONObject getConfigDetailsResponse(String fileName) {
		configServerUri = env.getProperty("spring.cloud.config.uri");
		configLabel = env.getProperty("spring.cloud.config.label");
		configProfile = env.getProperty("spring.profiles.active");
		configAppName = env.getProperty("spring.application.name");
		JSONObject result = null;
		try {
			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
					.append(configLabel + "/").append(fileName);

			restTemplate = new RestTemplate();
			result = restTemplate.getForObject(uriBuilder.toString(), JSONObject.class);
		} catch (Exception e) {
			throw new MasterDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.buildMessage(e.getMessage(), e.getCause()));

		}

		return result;

	}

}
