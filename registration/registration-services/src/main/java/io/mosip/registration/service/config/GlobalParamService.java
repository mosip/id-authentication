package io.mosip.registration.service.config;

import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;

/**
 * Service Class for GlobalContextParameters
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamService {

	/**
	 * Fetching Global parameters of application
	 * 
	 * @return map
	 */
	Map<String, Object> getGlobalParams();

	/**
	 * Get Global params details from server
	 * 
	 * @return response
	 */
	ResponseDTO synchConfigData();

}