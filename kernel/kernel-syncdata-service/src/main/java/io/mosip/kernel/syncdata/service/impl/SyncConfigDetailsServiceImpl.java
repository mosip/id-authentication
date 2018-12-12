package io.mosip.kernel.syncdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.syncdata.constant.SyncConfigDetailsErrorCode;
import io.mosip.kernel.syncdata.exception.MasterDataServiceException;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
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

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.registration.center.config.file.name}")
	private String regCenterfileName;

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.global.config.file.name}")
	private String globalConfigFileName;

	@Override
	public JSONObject getGlobalConfigDetails() {

		return getConfigDetailsResponse(globalConfigFileName);

	}

	@Override
	public JSONObject getRegistrationCenterConfigDetails(String regId) {

		return getConfigDetailsResponse(regCenterfileName);

	}

	/**
	 * This method will consume a REST API based on the filename passed.
	 * 
	 * @param fileName
	 *            - name of the file
	 * @return JSONObject
	 */
	private JSONObject getConfigDetailsResponse(String fileName) {
		String configServerUri = env.getProperty("spring.cloud.config.uri");
		String configLabel = env.getProperty("spring.cloud.config.label");
		String configProfile = env.getProperty("spring.profiles.active");
		String configAppName = env.getProperty("spring.application.name");
		JSONObject result = null;
		StringBuilder uriBuilder = null;
		if (fileName != null) {
			uriBuilder = new StringBuilder();
			uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
					.append(configLabel + "/").append(fileName);
		} else {
			throw new MasterDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETIAL_INPUT_PARAMETER_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETIAL_INPUT_PARAMETER_EXCEPTION.getErrorMessage());
		}
		try {
			result = restTemplate.getForObject(uriBuilder.toString(), JSONObject.class);
		} catch (RestClientException e) {
			throw new MasterDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.buildMessage(e.getMessage(), e.getCause()));

		}

		return result;

	}

}
