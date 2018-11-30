package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.MachineErrorCode;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineRequestDto;
import io.mosip.kernel.masterdata.dto.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.MachineSpecIdAndId;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.service.MachineService;
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

	@Autowired
	MachineHistoryRepository machineHistoryRepository;

	/**
	 * Field to hold ObjectMapperUtil object
	 */
	@Autowired
	MapperUtils objectMapperUtil;

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	/**
	 * Method used for retrieving Machine details based on given Machine ID and
	 * Language code
	 * 
	 * @param id
	 *            pass Machine ID as String
	 * 
	 * @param langCode
	 *            pass Language code as String
	 * 
	 * @return MachineDetailDto returning the Machine Detail for the given Machine
	 *         ID and Language code
	 * 
	 * @throws MachineDetailFetchException
	 *             While Fetching Machine Detail If fails to fetch required Machine
	 *             Detail
	 * 
	 * @throws MachineDetailMappingException
	 *             If not able to map Machine detail entity with Machine Detail Dto
	 * 
	 * @throws MachineDetailNotFoundException
	 *             If given required Machine ID and language not found
	 * 
	 */
	@Override
	public MachineDetailResponseIdDto getMachineDetailIdLang(String id, String langCode) {
		Machine machineDetail = null;
		MachineDto machineDetailDto = null;
		MachineDetailResponseIdDto machineDetailResponseIdDto = new MachineDetailResponseIdDto();
		try {
			machineDetail = machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(id, langCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetail != null) {
			machineDetailDto = objectMapperUtil.map(machineDetail, MachineDto.class);
		} else {

			throw new DataNotFoundException(MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());

		}
		machineDetailResponseIdDto.setMachineDto(machineDetailDto);
		return machineDetailResponseIdDto;

	}

	/**
	 * Method used for fetch all Machine details
	 * 
	 * @return MachineDetailDto returning all Machine Detail
	 * 
	 * @throws MachineDetailFetchException
	 *             While Fetching Machine Detail If fails to fetch required Machine
	 *             Detail
	 * 
	 * @throws MachineDetailMappingException
	 *             If not able to map Machine detail entity with Machine Detail Dto
	 * 
	 * @throws MachineDetailNotFoundException
	 *             If given required Machine ID and language not found
	 * 
	 */

	@Override
	public MachineResponseDto getMachineDetailAll() {
		List<Machine> machineDetailList = null;
		List<MachineDto> machineDetailDtoList = null;
		MachineResponseDto machineDetailResponseDto = new MachineResponseDto();
		try {
			machineDetailList = machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull();

		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetailList != null && !machineDetailList.isEmpty()) {
			machineDetailDtoList = objectMapperUtil.mapAll(machineDetailList, MachineDto.class);

		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		return machineDetailResponseDto;
	}

	/**
	 * Method used for retrieving Machine details based on given Language code
	 * 
	 * @param langCode
	 *            pass Language code as String
	 * 
	 * @return MachineDetailDto returning the Machine Detail for the given Language
	 *         code
	 * 
	 * @throws MachineDetailFetchException
	 *             While Fetching Machine Detail If fails to fetch required Machine
	 *             Detail
	 * 
	 * @throws MachineDetailMappingException
	 *             If not able to map Machine detail entity with Machine Detail Dto
	 * 
	 * @throws MachineDetailNotFoundException
	 *             If given required Machine ID and language not found
	 * 
	 */

	@Override
	public MachineResponseDto getMachineDetailLang(String langCode) {
		MachineResponseDto machineDetailResponseDto = new MachineResponseDto();
		List<Machine> machineDetailList = null;
		List<MachineDto> machineDetailDtoList = null;
		try {
			machineDetailList = machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetailList != null && !machineDetailList.isEmpty()) {
			machineDetailDtoList = objectMapperUtil.mapAll(machineDetailList, MachineDto.class);
		} else {
			throw new DataNotFoundException(MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		return machineDetailResponseDto;
	}

	@Override
	public MachineSpecIdAndId createMachine(MachineRequestDto machine) {
		Machine crtMachine;

		Machine entity = metaUtils.setCreateMetaData(machine.getRequest().getMachineDto(), Machine.class);
		MachineHistory entityHistory = metaUtils.createdMachineHistory(entity);

		try {
			crtMachine = machineRepository.create(entity);
			machineHistoryRepository.create(entityHistory);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(MachineErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					e.getErrorText());
		}
		MachineSpecIdAndId machineSpecIdAndId = new MachineSpecIdAndId();
		dataMapper.map(crtMachine, machineSpecIdAndId, true, null, null, true);

		return machineSpecIdAndId;
	}

}