package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;


/**
 * It does the pre registration check to ensure that the application can capture the Registration detail from individual. If the pre registration check
 * fails, then don't allow the application to capture the detail from individual.  Post completion of the respective sync process only 
 * the application can be used for Registration process. 
 * 
 * This would be called prior to New/ Update/ Lost UIN process. 
 *  
 * @author Mahesh Kumar
 */
public interface SyncStatusValidatorService {

	/**
	 * It does the following validation:
	 * <ul>
	 * <li>No. of Offline packet count shouldn't be greater than what is provided in the config. 
	 * <li>No. of days offline shouldn't be greater than what is provided in the config.
	 * <li>GPS related - center to machine distance shouldn't be greater than what is provided in the config.
	 * <li>No. of days, the application can run by skipping the Software update if available. 
	 * </ul>
	 * 
	 * 
	 * @return 
	 * 		Success or failure Error object along with the message. 
	 */
	public ResponseDTO validateSyncStatus();
	
	
	/**
	 * Check for force update of application. 
	 * 
	 * @return 
	 * 		true for Force Update.
	 */
	public boolean isToBeForceUpdate();
	
}
