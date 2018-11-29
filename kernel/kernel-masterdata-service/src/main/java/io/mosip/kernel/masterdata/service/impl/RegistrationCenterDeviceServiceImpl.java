package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class RegistrationCenterDeviceServiceImpl implements RegistrationCenterDeviceService {

	@Autowired
	private RegistrationCenterDeviceRepository registrationCenterDeviceRepository;

	@Autowired
	private MetaDataUtils metadataUtils;

	@Autowired
	private MapperUtils mapperUtils;

	@Override
	public ResponseRegistrationCenterDeviceDto mapRegistrationCenterAndDevice(
			RequestDto<RegistrationCenterDeviceDto> requestDto) {
		ResponseRegistrationCenterDeviceDto registrationCenterDeviceDto = null;
		try {
			RegistrationCenterDevice registrationCenterDevice = metadataUtils.setCreateMetaData(requestDto.getRequest(),
					RegistrationCenterDevice.class);

			RegistrationCenterDevice savedRegistrationCenterDevice = registrationCenterDeviceRepository
					.create(registrationCenterDevice);

			registrationCenterDeviceDto = mapperUtils.map(savedRegistrationCenterDevice,
					ResponseRegistrationCenterDeviceDto.class);

		} catch (Exception e) {
			// TODO remove stack-trace
			e.printStackTrace();
			throw new MasterDataServiceException("", "", e);
		}

		return registrationCenterDeviceDto;
	}

}
