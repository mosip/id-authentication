package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.RegistrationCenterDeviceHistoryResponseDto;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 */

public interface RegistrationCenterDeviceHistoryService {

	/**
	 * This abstract method to fetch registration center device history details for
	 * given registration id, device id and effective date time.
	 * 
	 * @param regCenterId
	 *            input Registration Center Id from User
	 * @param deviceId
	 *            input Device Id from user
	 * @param effDateTime
	 *            input effective date and time from user
	 * @return RegistrationCenterDeviceHistoryResponseDto Return Registration Center
	 *         Device History Detail for given regCenterId,deviceId and effDateTime
	 *
	 */
	RegistrationCenterDeviceHistoryResponseDto getRegCenterDeviceHisByregCenterIdDevIdEffDTime(String regCenterId,
			String deviceId, String effDateTime);

}
