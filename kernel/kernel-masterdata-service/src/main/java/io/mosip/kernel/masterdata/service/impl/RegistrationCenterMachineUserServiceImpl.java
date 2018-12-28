package io.mosip.kernel.masterdata.service.impl;

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
	RegistrationCenterMachineUserRepository  registrationCenterMachineUserRepository;
	
	/**
	 * Instance of {@link RegistrationCenterUserMachineHistoryRepository}
	 */
	@Autowired
	RegistrationCenterUserMachineHistoryRepository registrationCenterUserMachineHistoryRepository;
	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.RegistrationCenterMachineUserService#createRegistrationCentersMachineUserMapping(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public RegistrationCenterMachineUserID createRegistrationCentersMachineUserMapping(
			RequestDto<RegistrationCenterUserMachineMappingDto> registrationCenterUserMachineMappingDto) {
        
		RegistrationCenterUserMachine registrationCenterUserMachine=MetaDataUtils.setCreateMetaData(registrationCenterUserMachineMappingDto.getRequest(), RegistrationCenterUserMachine.class);
		RegistrationCenterUserMachineHistory registrationCenterUserMachineHistory=MetaDataUtils.setCreateMetaData(registrationCenterUserMachineMappingDto.getRequest(), RegistrationCenterUserMachineHistory.class);
		registrationCenterUserMachineHistory.setEffectivetimes(registrationCenterUserMachine.getCreatedDateTime());
		registrationCenterUserMachineHistory.setCreatedDateTime(registrationCenterUserMachine.getCreatedDateTime());
		
		try {
			registrationCenterMachineUserRepository.create(registrationCenterUserMachine);	
			registrationCenterUserMachineHistoryRepository.create(registrationCenterUserMachineHistory);
		}catch(DataAccessLayerException | DataAccessException exception) {
			throw new MasterDataServiceException(RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_INSERT_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineUserMappingErrorCode.REGISTRATION_CENTER_USER_MACHINE_MAPPING_INSERT_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(exception));
		}
		return MapperUtils.map(registrationCenterUserMachine, RegistrationCenterMachineUserID.class);
	}



}
