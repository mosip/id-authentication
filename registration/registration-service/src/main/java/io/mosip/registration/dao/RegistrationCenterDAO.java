package io.mosip.registration.dao;

import io.mosip.registration.dto.RegistrationCenterDetailDTO;

/**
 * DAO class for RegistrationCenter
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationCenterDAO {
	
	/**
	 * This method is used to get the Registration Center Mode
	 * 
	 * @return String
	 */
	
	public String getCenterName(String centerId);
	
	/**
	 * This method is used to get the Registration Center details
	 * 
	 * @return the list of {@link RegistrationCenterDetailDTO} based on the given center id
	 */
	
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId);

}
