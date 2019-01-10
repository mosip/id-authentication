package io.mosip.registration.dao;

import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public interface UserOnboardDAO {

	/**
	 * Insert.
	 *
	 * @param biometricDTO the biometric DTO
	 * @return the string
	 */
	String insert(BiometricDTO biometricDTO);
	
	/**
	 * Get Station ID by using mac address
	 * 
	 * @param MacAddress
	 *            machine address
	 * @return station ID
	 * @throws RegBaseCheckedException
	 */
	String getStationID(String MacAddress) throws RegBaseCheckedException;
	
	/**
	 * Get center ID using stationID
	 * 
	 * @param stationID
	 * @return center ID
	 * @throws RegBaseCheckedException
	 */
	String getCenterID(String stationID) throws RegBaseCheckedException;
	
}
