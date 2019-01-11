package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.postresponse.RegCenterMachineDeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDeviceHistory;

/**
 * This service handles CRUD operation for RegistrationCenter-Machine-Device history
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public interface RegistrationCenterMachineDeviceHistoryService {

	
    /**
     *  This method creates an entry in {@link RegistrationCenterMachineDeviceHistory} table whenever 
     *  create/update done in RegistrationCenter-Machine-Device
     * @param registrationCenterMachineDeviceHistoryDto - object that contain entity object
     * @return  RegCenterMachineDeviceHistoryResponseDto
     */
	public RegCenterMachineDeviceHistoryResponseDto createRegCenterMachineDeviceHistoryMapping(
			RegistrationCenterMachineDeviceHistory registrationCenterMachineDeviceHistory);
}
