package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistoryId;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * Implementation class for user machine mapping service
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class RegistrationCenterMachineUserServiceHistoryImpl implements RegistrationCenterMachineUserHistoryService {

	/**
	 * ModelMapper instance
	 */
	@Autowired
	private MapperUtils objectMapperUtil;

	/**
	 * {@link RegistrationCenterUserMachineHistoryRepository} instance
	 */
	@Autowired
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.
	 * RegistrationCenterMachineUserHistoryService#
	 * getRegistrationCentersMachineUserMapping(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			String effectiveTimestamp, String registrationCenterId, String machineId, String userId) {
		List<RegistrationCenterUserMachineHistory> registrationCenterUserMachines = null;
		try {
			registrationCenterUserMachines = registrationCenterUserMachineHistoryRepository
					.findByIdAndEffectivetimesLessThanEqualAndIsDeletedFalse(
							new RegistrationCenterUserMachineHistoryId(registrationCenterId, userId, machineId),
							LocalDateTime.parse(effectiveTimestamp));
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new MasterDataServiceException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorMessage());
		}
		if (registrationCenterUserMachines != null && registrationCenterUserMachines.isEmpty()) {
			throw new DataNotFoundException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorMessage());
		}

		List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters = null;
		registrationCenters = objectMapperUtil.mapAll(registrationCenterUserMachines,
				RegistrationCenterUserMachineMappingHistoryDto.class);
		RegistrationCenterUserMachineMappingHistoryResponseDto centerUserMachineMappingResponseDto = new RegistrationCenterUserMachineMappingHistoryResponseDto();
		centerUserMachineMappingResponseDto.setRegistrationCenters(registrationCenters);
		return centerUserMachineMappingResponseDto;
	}

}
