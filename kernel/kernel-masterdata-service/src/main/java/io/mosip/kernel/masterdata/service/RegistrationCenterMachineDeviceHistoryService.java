package io.mosip.kernel.masterdata.service;

import java.time.LocalDateTime;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterMachineDeviceDto;

/**
 * This service handles CRUD operation for RegistrationCenter-Machine-Device history
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public interface RegistrationCenterMachineDeviceHistoryService {

	
    /**
     * 
     * @param registrationCenterMachineDeviceHistoryDto
     * @return
     */
	public ResponseRegistrationCenterMachineDeviceDto createRegCenterMachineDeviceHistoryMapping(
			RegistrationCenterMachineDeviceHistoryDto registrationCenterMachineDeviceHistoryDto);
}
