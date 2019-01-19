package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.DeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.DeviceHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to fetch a Device History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface DeviceHistoryService {

	/**
	 * This abstract method to fetch device history details for given Device ID and
	 * language code and effDateTime
	 * 
	 * @param id
	 *            Device id given by user
	 * @param langCode
	 *            Language code given by user
	 * @param effDateTime
	 *            Effective date and time given by user
	 * @return DeviceHistoryResponseDto Return Device History Detail for given
	 *         device id and language code
	 *
	 */
	DeviceHistoryResponseDto getDeviceHistroyIdLangEffDTime(String id, String langCode, String effDateTime);

	
	/**
	 * Abstract method to save Device History to the Database
	 * 
	 * @param entityHistory
	 *            device History entity 
	 * 
	 * @return IdResponseDto returning device History id which is inserted successfully
	 *         {@link IdResponseDto}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Device History
	 */
	IdResponseDto createDeviceHistory(DeviceHistory entityHistory);
}
