package io.mosip.registration.service.config;

import java.sql.Timestamp;
import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;

/**
 * It interface with the external 'global' service and sync the application specific configuration data from server to local machine. 
 * It stores the downloaded configuration detail into local db that will be used by the application through out the life cycle. 
 * 
 * This service is invoked during start of the application OR post successful login to the application based on use cases.  
 * 
 * @author Sravya Surampalli
 * @author Yaswanth
 * @author Brahmananda Reddy
 * @since 1.0.0
 */
public interface GlobalParamService {

	/**
	 * Fetching application configuration detail from Global parameters table, that will be used by the application.  
	 * 
	 * @return map
	 * 		It contains the key and value pair of each configuration.  
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
	 * 			It contains the SuccessResponseDTO, where the flag value would be available. 
	 */
	ResponseDTO updateSoftwareUpdateStatus(boolean isUpdateAvailable,Timestamp timestamp);

	/**
	 * Update global param table with particular key value pair which is download from the external system. 
	 * 
	 * @param code
	 *            global param code
	 * @param val
	 *            value 
	 */
	void update(String code, String val);

}