package io.mosip.registration.dao;

import java.util.Map;

/**
 * DAO class for GlobalParam
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamDAO {
	
	/**
	 * This method is used to get the global params
	 * 
	 * @return Map of global params
	 */
	Map<String,Object> getGlobalParams();
}