package io.mosip.registration.dao;

import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.RegistrationCenter;

/**
 * This class is used to get the Registration Center details from {@link RegistrationCenter} 
 * table by passing center id and language code as parameters.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface RegistrationCenterDAO {

	/**
	 * This method is used to get the Registration Center details from {@link RegistrationCenter} 
	 * table by passing center id and language code as parameters.
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
