package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterDeviceErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRegistrationCenterDeviceDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterDeviceRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterDeviceService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
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
	private RegistrationCenterDeviceHistoryRepository registrationCenterDeviceHistoryRepository;

	/**
	 * (non-Javadoc)
	 * 
	 * @see RegistrationCenterDeviceService#createRegistrationCenterAndDevice(RequestDto)
	 */
	@Override
	public ResponseRegistrationCenterDeviceDto createRegistrationCenterAndDevice(
			RequestDto<RegistrationCenterDeviceDto> requestDto) {
		ResponseRegistrationCenterDeviceDto registrationCenterDeviceDto = null;
		try {
			RegistrationCenterDevice registrationCenterDevice = MetaDataUtils.setCreateMetaData(requestDto.getRequest(),
					RegistrationCenterDevice.class);
			RegistrationCenterDevice savedRegistrationCenterDevice = registrationCenterDeviceRepository
					.create(registrationCenterDevice);

			RegistrationCenterDeviceHistory registrationCenterDeviceHistory = MetaDataUtils
					.setCreateMetaData(requestDto.getRequest(), RegistrationCenterDeviceHistory.class);
			registrationCenterDeviceHistory.setEffectivetimes(registrationCenterDeviceHistory.getCreatedDateTime());
			registrationCenterDeviceHistoryRepository.create(registrationCenterDeviceHistory);

			registrationCenterDeviceDto = MapperUtils.map(savedRegistrationCenterDevice.getRegistrationCenterDevicePk(),
					ResponseRegistrationCenterDeviceDto.class);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterDeviceErrorCode.REGISTRATION_CENTER_DEVICE_CREATE_EXCEPTION.getErrorMessage()
							+ ": " + ExceptionUtils.parseException(e));
		}

		return registrationCenterDeviceDto;
	}

}
