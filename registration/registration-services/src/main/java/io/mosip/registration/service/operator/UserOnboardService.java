package io.mosip.registration.service.operator;

import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;

/**
 * The {@code UserOnboardService} represents to validate the user bio-metirc details against the IDA.
 * It calls the IDA server and send the information in the form user bio-metric DTO information of 
 * fingerprint,iris and face] on successful validation the same will be persisted into the Database.
 * 
 *  
 * @author Sreekar Chukka
 * 
 */
public interface UserOnboardService {

	/**
	 * Validate.
	 *
	 * @param biometricDTO the biometric DTO
	 * @return the response DTO
	 */
	ResponseDTO validate(BiometricDTO biometricDTO);
	
	/**
	 * Gets the station ID.
	 *
	 * @return the station ID
	 */
	Map<String,String> getMachineCenterId();
		
	
	
	
}
