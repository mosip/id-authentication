package io.mosip.kernel.syncdata.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.syncdata.constant.SyncConfigDetailsErrorCode;
import io.mosip.kernel.syncdata.dto.ConfigDto;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigDetailsServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.syncdata.registration-center-config-file2}")
	private String regCenterfileName;

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.syncdata.global-config-file2}")
	private String globalConfigFileName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.syncdata.service.SyncConfigDetailsService#getConfigDetails()
	 */
	@Override
	public JSONObject getConfigDetails() {
		JSONObject config = new JSONObject();
		JSONObject globalConfig = getConfigDetailsResponse(globalConfigFileName);
		JSONObject regConfig = getConfigDetailsResponse(regCenterfileName);
		config.put("globalConfiguration", globalConfig);
		config.put("registrationConfiguration", regConfig);
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.syncdata.service.SyncConfigDetailsService#
	 * getGlobalConfigDetails()
	 */
	@Override
	public JSONObject getGlobalConfigDetails() {

		return getConfigDetailsResponse(globalConfigFileName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.syncdata.service.SyncConfigDetailsService#
	 * getRegistrationCenterConfigDetails(java.lang.String)
	 */
	@Override
	public JSONObject getRegistrationCenterConfigDetails(String regId) {

		return getConfigDetailsResponse(regCenterfileName);

	}

	public ConfigDto getConfiguration(String registrationCenterId) {
		ConfigDto configDto = null;
		configDto = new ConfigDto();
		configDto.setGlobalConfig(getGlobalConfigDetails());
		configDto.setRegistrationCenterConfiguration(getRegistrationCenterConfigDetails(registrationCenterId));
		return configDto;
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
			throw new SyncDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_INPUT_PARAMETER_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_INPUT_PARAMETER_EXCEPTION.getErrorMessage());
		}
		try {
			String str = restTemplate.getForObject(uriBuilder.toString(), String.class);
			Properties prop = parsePropertiesString(str);
			result = new JSONObject();
			for (Entry<Object, Object> e : prop.entrySet()) {
				result.put(String.valueOf(e.getKey()), e.getValue());
			}
		} catch (RestClientException | IOException e) {
			throw new SyncDataServiceException(
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorCode(),
					SyncConfigDetailsErrorCode.SYNC_CONFIG_DETAIL_REST_CLIENT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.buildMessage(e.getMessage(), e.getCause()));

		}

		return result;

	}

	public Properties parsePropertiesString(String s) throws IOException {
		final Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}

}
