package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

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
	 * @param registrationCenterId registrationCenterId
	 * @param langCode             langCode
	 * @param effectiveDate        effectiveDate
	 * @return {@link RegistrationCenterDto}
	 */
	RegistrationCenterHistoryResponseDto getRegistrationCenterHistory(String registrationCenterId, String langCode,
			String effectiveDate);

	/**
	 * Abstract method to save RegistrationCenter History to the Database
	 * 
	 * @param entityHistory machine History entity
	 * 
	 * @return IdResponseDto returning RegistrationCenter History id which is
	 *         inserted successfully {@link IdResponseDto}
	 * 
	 * @throws MasterDataServiceException if any error occurred while saving
	 *                                    RegistrationCenter History
	 */
	public IdResponseDto createRegistrationCenterHistory(RegistrationCenterHistory entityHistory);
}
