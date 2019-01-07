package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceHistoryService;

public class RegCenterMachineDeviceHistoryServiceImpl implements RegistrationCenterMachineDeviceHistoryService {

	@Autowired
	RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepo;

	@Override
	public ResponseRegistrationCenterMachineDeviceDto createRegCenterMachineDeviceHistoryMapping(
			RegistrationCenterMachineDeviceHistoryDto registrationCenterMachineDeviceHistoryDto) {
		
		return null;
	}

}
