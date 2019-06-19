package io.mosip.registration.service.config;

import java.sql.Timestamp;
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
	 * It makes call to the external service 'global' to download the configuration from MOSIP server and sync the same with the local db.
	 * After downloading it does the following while updating db: 
	 * <ul> 
	 * <li>If any additional config available in local then delete the same. 
	 * <li>If any additional config received from remote then insert the same.
	 * <li>If any updated received for the existing one then modify the same at local db. 
	 * </ul>
	 * 
	 * @param isJob
	 *            whaeather it is triggerred by job or not
	 * 
	 * @return response
	 * 			return success or failure response along with error based on service response. 
	 */
	ResponseDTO synchConfigData(boolean isJob);

	/**
	 * It checks the software update status flag from database and respond with 'Y' or 'N' value.
	 * 
	 * @param isUpdateAvailable
	 *          - update status
	 * @param timestamp
	 * 			- the timestamp
	 *
	 * @return the response DTO
	 */
	ResponseDTO updateSoftwareUpdateStatus(boolean isUpdateAvailable,Timestamp timestamp);

	/**
	 * Update global param table with particular key value pair. 
	 * 
	 * @param code
	 *            global param code
	 * @param val
	 *            value
	 */
	void update(String code, String val);

}