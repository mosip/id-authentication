package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.service.MachineService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch a Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineServiceImpl implements MachineService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineRepository machineRepository;
	/**
	 * Field to hold Machine History  Repository object
	 */
	@Autowired
	MachineHistoryRepository machineHistoryRepository;


	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachine(java.lang.String, java.lang.String)
	 */
	@Override
	public MachineResponseDto getMachine(String id, String langCode) {
		List<Machine> machineList = null;
		List<MachineDto> machineDtoList = null;
		MachineResponseDto machineResponseIdDto = new MachineResponseDto();
		try {
			machineList = machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(id, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);
		} else {

			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());

		}
		machineResponseIdDto.setMachines(machineDtoList);
		return machineResponseIdDto;
	}

	

	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachineAll()
	 */
	@Override
	public MachineResponseDto getMachineAll() {
		List<Machine> machineList = null;

		List<MachineDto> machineDtoList = null;
		MachineResponseDto machineResponseDto = new MachineResponseDto();
		try {
			machineList = machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull();

		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);

		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineResponseDto.setMachines(machineDtoList);
		return machineResponseDto;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineService#getMachine(java.lang.String)
	 */
	@Override
	public MachineResponseDto getMachine(String langCode) {
		MachineResponseDto machineResponseDto = new MachineResponseDto();
		List<Machine> machineList = null;
		List<MachineDto> machineDtoList = null;
		try {
			machineList = machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_FETCH_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}
		if (machineList != null && !machineList.isEmpty()) {
			machineDtoList = MapperUtils.mapAll(machineList, MachineDto.class);

		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineResponseDto.setMachines(machineDtoList);
		return machineResponseDto;
	}

	
	/* (non-Javadoc)
	 * @see io.mosip.kernel.masterdata.service.MachineService#createMachine(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public IdResponseDto createMachine(RequestDto<MachineDto> machine) {
		Machine crtMachine = null;
		Machine entity = MetaDataUtils.setCreateMetaData(machine.getRequest(), Machine.class);
		MachineHistory entityHistory = MetaDataUtils.setCreateMetaData(machine.getRequest(), MachineHistory.class);
		entityHistory.setEffectDateTime(entity.getCreatedDateTime());
		entityHistory.setCreatedDateTime(entity.getCreatedDateTime());
		try {
			crtMachine = machineRepository.create(entity);
			machineHistoryRepository.create(entityHistory);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		MapperUtils.map(crtMachine, idResponseDto);

		return idResponseDto;
	}

}