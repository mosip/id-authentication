package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.MachineDetailErrorCode;
import io.mosip.kernel.masterdata.dto.MachineDetailDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseDto;
import io.mosip.kernel.masterdata.dto.MachineDetailResponseIdDto;
import io.mosip.kernel.masterdata.entity.MachineDetail;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineDetailRepository;
import io.mosip.kernel.masterdata.service.MachineDetailService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * This class have methods to fetch a Machine Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineDetailServiceImpl implements MachineDetailService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineDetailRepository machineDetailRepository;

	/**
	 * Field to hold ObjectMapperUtil object
	 */
	@Autowired
	ObjectMapperUtil objectMapperUtil;

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
		MachineDetail machineDetail = null;
		MachineDetailDto machineDetailDto = null;
		MachineDetailResponseIdDto machineDetailResponseIdDto = new MachineDetailResponseIdDto();
		try {
			machineDetail = machineDetailRepository.findAllByIdAndLangCodeAndIsDeletedFalse(id,
					langCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetail != null) {
			machineDetailDto = objectMapperUtil.map(machineDetail, MachineDetailDto.class);
		} else {

			throw new DataNotFoundException(MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());

		}
		machineDetailResponseIdDto.setMachineDetail(machineDetailDto);
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
	public MachineDetailResponseDto getMachineDetailAll() {
		List<MachineDetail> machineDetailList = null;
		List<MachineDetailDto> machineDetailDtoList = null;
		MachineDetailResponseDto machineDetailResponseDto = new MachineDetailResponseDto();
		try {
			machineDetailList = machineDetailRepository.findAllByIsDeletedFalse();

		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetailList != null && !machineDetailList.isEmpty()) {
			machineDetailDtoList = objectMapperUtil.mapAll(machineDetailList, MachineDetailDto.class);

		} else {
			throw new DataNotFoundException(MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());
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
	public MachineDetailResponseDto getMachineDetailLang(String langCode) {
		MachineDetailResponseDto machineDetailResponseDto = new MachineDetailResponseDto();
		List<MachineDetail> machineDetailList = null;
		List<MachineDetailDto> machineDetailDtoList = null;
		try {
			machineDetailList = machineDetailRepository.findAllByLangCodeAndIsDeletedFalse(langCode);
		} catch (DataAccessException dataAccessLayerException) {
			throw new MasterDataServiceException(MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_FETCH_EXCEPTION.getErrorMessage());
		}
		if (machineDetailList != null && !machineDetailList.isEmpty()) {
			machineDetailDtoList = objectMapperUtil.mapAll(machineDetailList, MachineDetailDto.class);
		} else {
			throw new DataNotFoundException(MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorCode(),
					MachineDetailErrorCode.MACHINE_DETAIL_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		machineDetailResponseDto.setMachineDetails(machineDetailDtoList);
		return machineDetailResponseDto;
	}

}