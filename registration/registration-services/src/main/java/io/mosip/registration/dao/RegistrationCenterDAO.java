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
	 * This method is used to get the Registration Center details
	 * 
	 * @param centerId
	 *            id of the center
	 * @param langCode
	 *            language code
	 * 
	 * @return the list of {@link RegistrationCenterDetailDTO} based on the given
	 *         center id
	 */

	RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId, String langCode);

}
