package io.mosip.registration.service.operator;

import java.sql.Timestamp;
import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;

/**
 * The {@code UserOnboardService} represents to validate the user bio-metirc details against the IDA.
 * It calls the IDA server and send the information in the form of user bio-metric DTO{@code BiometricDTO} information of 
 * fingerprint,iris and face on successful validation the same will be persisted into the Database.
 * 
 *  
 * @author Sreekar Chukka
 * 
 */
public interface UserOnboardService {

	/**
	 * This method performs to invoke the user onBoarding by validating Biometric's
	 * provided by the user while onBoarding.
	 * 
	 * While onBoarding user will be providing his/her Biometric information,which
	 * will be validated against the IDA. the online connectivity check will be
	 * performed as preliminary step to connect to the server for validating the
	 * biometrics.
	 * 
	 * If Online : The server call performs and based on the result the return
	 * response will be formed.
	 * 
	 * If Success: The Response returns True Biometric information will be stored in
	 * {@code BiometricDTO} and persist to the database. The success response DTO
	 * will be formed and returned from this method.
	 * 
	 * If Failure: The failure response DTO will be formed and returned from this
	 * method.
	 * 
	 * If Offline: The failure response DTO will be formed and returned from this
	 * method.
	 * 
	 *
	 * @param biometricDTO {@code BiometricDTO} which holds the bio-metric
	 *                     information of the user.
	 * 
	 * @return {@code ResponseDTO} based on the result the response DTO will be
	 *         formed and return to the caller.
	 */
	ResponseDTO validate(BiometricDTO biometricDTO);
	
	/**
	 * This method performs to get center-id and machine-id from db.
	 * 
	 * First the machine mac-id will be picked up , The center-id will be picked up
	 * based on the mac-id of the machine.
	 * 
	 * @return {@code Map} Result will be a map contains machine-id,center-id and
	 *         return to the caller.
	 */
	Map<String,String> getMachineCenterId();
		
	/**
	 * Gets the last updated operator bio-metric date time.
	 *
	 * @param usrID the usr ID
	 * @return the last updated operator bio-metric date time
	 */
	Timestamp getLastUpdatedTime(String usrId);
	
	
}
