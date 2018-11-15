package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterUserMappingHistoryErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingHistoryResponseDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistoryId;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingFetchHistoryException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingHistoryException;
import io.mosip.kernel.masterdata.exception.RegistrationCenterUserMachineMappingNotFoundHistoryException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService;
/** Implementation class for user machine mapping service 
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
	ModelMapper modelMapper;
	/**
	 * {@link RegistrationCenterUserMachineHistoryRepository} instance
	 */
	@Autowired
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserHistoryService#getRegistrationCentersMachineUserMapping(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterUserMachineMappingHistoryResponseDto getRegistrationCentersMachineUserMapping(
			String effectiveTimestamp, String registrationCenterId, String machineId, String userId) {
		List<RegistrationCenterUserMachineHistory> registrationCenterUserMachines = null;
		try {
			registrationCenterUserMachines = registrationCenterUserMachineHistoryRepository
					.findByIdAndEffectivetimesLessThanEqual(
							new RegistrationCenterUserMachineHistoryId(registrationCenterId, userId, machineId),
							LocalDateTime.parse(effectiveTimestamp));
		} catch (DataAccessLayerException dataAccessLayerException) {
			throw new RegistrationCenterUserMachineMappingFetchHistoryException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_FETCH_EXCEPTION
							.getErrorMessage());
		}
		if (registrationCenterUserMachines.isEmpty()) {
			throw new RegistrationCenterUserMachineMappingNotFoundHistoryException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_NOT_FOUND
							.getErrorMessage());
		}

		List<RegistrationCenterUserMachineMappingHistoryDto> registrationCenters = null;
		try {
			registrationCenters = modelMapper.map(registrationCenterUserMachines,
					new TypeToken<List<RegistrationCenterUserMachineMappingHistoryDto>>() {
					}.getType());
		} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
			throw new RegistrationCenterUserMachineMappingHistoryException(
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_EXCEPTION
							.getErrorCode(),
					RegistrationCenterUserMappingHistoryErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_HISTORY_EXCEPTION
							.getErrorMessage());
		}
		RegistrationCenterUserMachineMappingHistoryResponseDto centerUserMachineMappingResponseDto = new RegistrationCenterUserMachineMappingHistoryResponseDto();
		centerUserMachineMappingResponseDto.setRegistrationCenters(registrationCenters);
		return centerUserMachineMappingResponseDto;
	}

}
