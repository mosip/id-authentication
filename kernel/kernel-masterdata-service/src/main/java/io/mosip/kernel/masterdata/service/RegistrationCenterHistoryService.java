package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterHistoryResponseDto;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface RegistrationCenterHistoryService {

	/**
	 * Function to fetch specific registration center history detail by registration
	 * center id
	 * 
	 * @param registrationCenterId
	 * @param effectiveDate
	 * @return {@link RegistrationCenterDto}
	 */
	RegistrationCenterHistoryResponseDto getRegistrationCenterHistory(String registrationCenterId, String langCode,
			String effectiveDate);
}
