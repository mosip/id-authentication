package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;

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

	/**
	 * This method saves the list of globalparam
	 * 
	 * @param list
	 *            list of global params
	 */

	void saveAll(List<GlobalParam> list);

	/**
	 * This method is used to get the globalparam
	 * 
	 * @param globalParamId
	 *            is a primary key
	 * @return GlobalParam against the primary key
	 */

	GlobalParam get(GlobalParamId globalParamId);

	/**
	 * Get All Global params
	 * 
	 * @param names
	 *            global param names
	 * @return list of global params
	 */
	List<GlobalParam> getAll(List<String> names);

	/**
	 * 
	 * @return list of global params
	 */
	List<GlobalParam> getAllEntries();

	/**
	 * Update software update status.
	 *
	 * @param isUpdateAvailable
	 *            the status
	 * @param timestamp - the timestamp
	 * @return the global param
	 */
	GlobalParam updateSoftwareUpdateStatus(boolean isUpdateAvailable,Timestamp timestamp);
	
	/**
	 * Update Global Param
	 * @param globalParam to be updated
	 * @return global Param
	 */
	GlobalParam update(GlobalParam globalParam);

}