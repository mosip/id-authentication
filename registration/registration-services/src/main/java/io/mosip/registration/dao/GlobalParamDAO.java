package io.mosip.registration.dao;

import java.util.List;
import java.util.Map;

import io.mosip.registration.entity.GlobalParam;

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
	Map<String, Object> getGlobalParams();

	void saveAll(List<GlobalParam> list);

	GlobalParam get(String name);

	/**
	 * Get All Global params
	 * 
	 * @param names
	 *            global param names
	 * @return list of global params
	 */
	List<GlobalParam> getAll(List<String> names);

}