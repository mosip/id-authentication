package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterMachineDeviceHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDeviceHistoryDto;
import io.mosip.kernel.masterdata.dto.postresponse.RegCenterMachineDeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineDeviceHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineDeviceHistoryService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * Class contains bussiness logics for the interface
 * {@link RegistrationCenterMachineDeviceHistoryService}
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */

@Service
public class RegCenterMachineDeviceHistoryServiceImpl implements RegistrationCenterMachineDeviceHistoryService {

	/**
	 * creates instance of {@link RegistrationCenterMachineDeviceHistoryRepository}
	 */
	@Autowired
	RegistrationCenterMachineDeviceHistoryRepository registrationCenterMachineDeviceHistoryRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.
	 * RegistrationCenterMachineDeviceHistoryService#
	 * createRegCenterMachineDeviceHistoryMapping(io.mosip.kernel.masterdata.entity.
	 * RegistrationCenterMachineDeviceHistory)
	 */
	@Override
	public RegCenterMachineDeviceHistoryResponseDto createRegCenterMachineDeviceHistoryMapping(
			RegistrationCenterMachineDeviceHistory registrationCenterMachineDeviceHistory) {
		RegistrationCenterMachineDeviceHistoryDto registrationCenterMachineDeviceHistoryDto = null;
		RegCenterMachineDeviceHistoryResponseDto regCenterMachineDeviceHistoryResponseDto = new RegCenterMachineDeviceHistoryResponseDto();
		try {
			registrationCenterMachineDeviceHistoryRepo.create(registrationCenterMachineDeviceHistory);
		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineDeviceHistoryErrorCode.REGISTRATION_CENTER_MACHINE_DEVICE_HISTORY_CREATE_EXCEPTION
							.getErrorCode(),
					RegistrationCenterMachineDeviceHistoryErrorCode.REGISTRATION_CENTER_MACHINE_DEVICE_HISTORY_CREATE_EXCEPTION
							.getErrorMessage());
		}

		registrationCenterMachineDeviceHistoryDto = MapperUtils.map(registrationCenterMachineDeviceHistory,
				RegistrationCenterMachineDeviceHistoryDto.class);
		regCenterMachineDeviceHistoryResponseDto
				.setRegistrationCenterMachineDeviceHistoryDto(registrationCenterMachineDeviceHistoryDto);

		return regCenterMachineDeviceHistoryResponseDto;
	}

}
