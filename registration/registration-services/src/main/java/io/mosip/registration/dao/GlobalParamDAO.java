package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;

/**
 * This class is used to fetch/save/update the Global Parameter.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalParamDAO {

	/**
	 * This method is used to get the global parameters
	 * 
	 * @return Map of global parameters
	 */
	Map<String, Object> getGlobalParams();

	/**
	 * This method is used to save all the list of global parameters
	 * 
	 * @param list
	 *            list of global params
	 */

	void saveAll(List<GlobalParam> list);

	/**
	 * This method is used to get the global parameters by {@link GlobalParamId}.
	 * 
	 * @param globalParamId
	 *            is a primary key
	 * @return GlobalParam against the primary key
	 */

	GlobalParam get(GlobalParamId globalParamId);

	/**
	 * This method is used to get all the global parameters which are passed as parameter.
	 * 
	 * @param names
	 *            global parameter names
	 * @return list of global parameters
	 */
	List<GlobalParam> getAll(List<String> names);

	/**
	 * This method is used to get all the global parameters
	 * 
	 * @return list of global params
	 */
	List<GlobalParam> getAllEntries();

	/**
	 * This method is used to update software update status flag and the time when the software 
	 * update was available in global param table if software update available.
	 *
	 * @param isUpdateAvailable
	 *              the status that need to be updated.
	 * @param timestamp 
	 * 				the timestamp
	 * @return the global param after updating the flag in the table
	 */
	GlobalParam updateSoftwareUpdateStatus(boolean isUpdateAvailable,Timestamp timestamp);
	
	/**
	 * This method is used to update particular global param.
	 * @param globalParam that needs to be updated
	 * @return global Param after updating the flag in the table
	 */
	GlobalParam update(GlobalParam globalParam);

}