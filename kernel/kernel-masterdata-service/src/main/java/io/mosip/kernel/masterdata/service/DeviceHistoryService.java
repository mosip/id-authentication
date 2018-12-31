package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.DeviceHistoryResponseDto;

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

}
