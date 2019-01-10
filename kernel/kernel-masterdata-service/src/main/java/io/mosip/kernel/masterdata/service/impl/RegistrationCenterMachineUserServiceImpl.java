package io.mosip.kernel.masterdata.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterMachineUserMappingErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterUserMachineMappingDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineUserRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterUserMachineHistoryRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Implementation class for user machine mapping service
 * 
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 * @see RegistrationCenterMachineUserService
 *
 */
@Service
public class RegistrationCenterMachineUserServiceImpl implements RegistrationCenterMachineUserService {

	/**
	 * Instance of {@link RegistrationCenterMachineUserRepository}
	 */
	@Autowired
	RegistrationCenterMachineUserRepository registrationCenterMachineUserRepository;

	/**
	 * Instance of {@link RegistrationCenterUserMachineHistoryRepository}
	 */
	@Autowired
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService#
	 * createRegistrationCentersMachineUserMapping(io.mosip.kernel.masterdata.dto.
	 * RequestDto)
	 */
	@Override
	@Transactional
	public RegistrationCenterMachineUserID createRegistrationCentersMachineUserMapping(
			RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto) {

		RegistrationCenterUserMachine registrationCenterUserMachine = MetaDataUtils.setCreateMetaData(
				registrationCenterUserMachineMappingDto.getRequest(), RegistrationCenterUserMachine.class);
		RegistrationCenterUserMachineHistory registrationCenterUserMachineHistory = MetaDataUtils.setCreateMetaData(
				registrationCenterUserMachineMappingDto.getRequest(), RegistrationCenterUserMachineHistory.class);
		registrationCenterUserMachineHistory.setEffectivetimes(registrationCenterUserMachine.getCreatedDateTime());
		registrationCenterUserMachineHistory.setCreatedDateTime(registrationCenterUserMachine.getCreatedDateTime());

		try {
			registrationCenterMachineUserRepository.create(registrationCenterUserMachine);
			registrationCenterUserMachineHistoryRepository.create(registrationCenterUserMachineHistory);
		} catch (DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_INSERT_EXCEPTION
							.getErrorCode(),
					RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_INSERT_EXCEPTION
							.getErrorMessage() + ExceptionUtils.parseException(exception));
		}
		return MapperUtils.map(registrationCenterUserMachine, RegistrationCenterMachineUserID.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService#
	 * deleteRegistrationCentersMachineUserMapping(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public RegistrationCenterMachineUserID deleteRegistrationCentersMachineUserMapping(String regCenterId,
			String machineId, String userId) {
		RegistrationCenterMachineUserID registrationCenterMachineUserID = null;
		try {

			Optional<RegistrationCenterUserMachine> registrationCenterUserMachine = registrationCenterMachineUserRepository
					.findAllNondeletedMappings(regCenterId, machineId, userId);
			if (!registrationCenterUserMachine.isPresent()) {
				throw new RequestException(
						RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_NOT_FOUND
								.getErrorCode(),
						RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_NOT_FOUND
								.getErrorMessage());
			} else {
				RegistrationCenterUserMachine centerUserMachine = registrationCenterUserMachine.get();
				centerUserMachine = MetaDataUtils.setDeleteMetaData(centerUserMachine);
				RegistrationCenterUserMachineHistory history = MapperUtils.map(centerUserMachine,
						RegistrationCenterUserMachineHistory.class);
				history.setCntrId(regCenterId);
				history.setMachineId(machineId);
				history.setUsrId(userId);
				history.setEffectivetimes(centerUserMachine.getDeletedDateTime());
				MapperUtils.setBaseFieldValue(centerUserMachine, history);
				registrationCenterUserMachineHistoryRepository.create(history);
				registrationCenterMachineUserRepository.update(centerUserMachine);
				registrationCenterMachineUserID = new RegistrationCenterMachineUserID();
				registrationCenterMachineUserID.setCntrId(regCenterId);
				registrationCenterMachineUserID.setMachineId(machineId);
				registrationCenterMachineUserID.setUsrId(userId);
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_DELETE_EXCEPTION
							.getErrorCode(),
					RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_DELETE_EXCEPTION
							.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		return registrationCenterMachineUserID;
	}

}
