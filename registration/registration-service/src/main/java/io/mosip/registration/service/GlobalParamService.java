package io.mosip.registration.service;

import java.util.Map;


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
	 * @return map
	 */
	Map<String,Object> getGlobalParams();

}