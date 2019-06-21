package io.mosip.registration.service.sync;

import io.mosip.registration.dto.ResponseDTO;


/**
 * It does the pre check before doing registration to ensure that the application can capture the Registration detail from individual. If the pre check
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
	 * This method helps to check whether application should perform force software update or not. It checks the software update flag value in the database table. 
	 * If 'Y' then validate the days skipped for update. If the days are greater than the configured one at the properties then this method would return 
	 * true otherwise false.  
	 * 
	 * This method would be invoked, during start up of the application.  
	 * 
	 * @return 
	 * 		true for Force Update.
	 */
	public boolean isToBeForceUpdate();
	
}
