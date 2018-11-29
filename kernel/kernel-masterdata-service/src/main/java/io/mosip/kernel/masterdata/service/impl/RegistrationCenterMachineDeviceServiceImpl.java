package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDeviceDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class RegistrationCenterMachineDeviceServiceImpl implements RegistrationCenterMachineDeviceService {

	@Autowired
	private RegistrationCenterMachineDeviceRepository registrationCenterMachineDeviceRepository;

	@Autowired
	private MetaDataUtils metadataUtils;

	@Autowired
	private MapperUtils mapperUtils;

	@Override
	public ResponseRrgistrationCenterMachineDeviceDto mapRegistrationCenterMachineAndDevice(
			RequestDto<RegistrationCenterMachineDeviceDto> requestDto) {
		ResponseRrgistrationCenterMachineDeviceDto responseRrgistrationCenterMachineDeviceDto = null;

		try {
			RegistrationCenterMachineDevice registrationCenterMachineDevice = metadataUtils
					.setCreateMetaData(requestDto.getRequest(), RegistrationCenterMachine.class);

			RegistrationCenterMachineDevice savedRegistrationCenterMachineDevice = registrationCenterMachineDeviceRepository
					.create(registrationCenterMachineDevice);

			responseRrgistrationCenterMachineDeviceDto = mapperUtils.map(savedRegistrationCenterMachineDevice,
					ResponseRrgistrationCenterMachineDeviceDto.class);
		} catch (Exception e) {
			new MasterDataServiceException("", "", e);
		}

		return responseRrgistrationCenterMachineDeviceDto;
	}

}
