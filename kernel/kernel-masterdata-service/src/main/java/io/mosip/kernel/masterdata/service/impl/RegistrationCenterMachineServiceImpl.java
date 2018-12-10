package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.RegistrationCenterMachineErrorCode;
import io.mosip.kernel.masterdata.dto.RegistrationCenterMachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.ResponseRrgistrationCenterMachineDto;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachine;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterMachineRepository;
import io.mosip.kernel.masterdata.service.RegistrationCenterMachineService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Service
public class RegistrationCenterMachineServiceImpl implements RegistrationCenterMachineService {

	@Autowired
	private RegistrationCenterMachineRepository registrationCenterMachineRepository;
	@Autowired
	private RegistrationCenterMachineHistoryRepository registrationCenterMachineHistoryRepository;


	@Override
	public ResponseRrgistrationCenterMachineDto createRegistrationCenterAndMachine(
			RequestDto<RegistrationCenterMachineDto> requestDto) {
		ResponseRrgistrationCenterMachineDto responseRrgistrationCenterMachineDto = null;

		try {
			RegistrationCenterMachine registrationCenterMachine = MetaDataUtils
					.setCreateMetaData(requestDto.getRequest(), RegistrationCenterMachine.class);
			RegistrationCenterMachine savedRegistrationCenterMachine = registrationCenterMachineRepository
					.create(registrationCenterMachine);

			RegistrationCenterMachineHistory registrationCenterMachineHistory = MetaDataUtils
					.setCreateMetaData(requestDto.getRequest(), RegistrationCenterMachineHistory.class);
			registrationCenterMachineHistory.setEffectivetimes(savedRegistrationCenterMachine.getCreatedDateTime());
			registrationCenterMachineHistoryRepository.create(registrationCenterMachineHistory);

			responseRrgistrationCenterMachineDto = MapperUtils.map(
					savedRegistrationCenterMachine.getRegistrationCenterMachinePk(),
					ResponseRrgistrationCenterMachineDto.class);
		} catch (DataAccessLayerException  | DataAccessException   e) {
			throw new MasterDataServiceException(
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION.getErrorCode(),
					RegistrationCenterMachineErrorCode.REGISTRATION_CENTER_MACHINE_CREATE_EXCEPTION.getErrorMessage()
							+ " " + ExceptionUtils.parseException(e));
		}

		return responseRrgistrationCenterMachineDto;
	}

}
