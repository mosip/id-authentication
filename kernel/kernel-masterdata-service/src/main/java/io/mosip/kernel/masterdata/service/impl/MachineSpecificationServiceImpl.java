package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineSpecificationErrorCode;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.service.MachineSpecificationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
/**
 * This class have methods to save  a Machine Specification Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineSpecificationServiceImpl implements MachineSpecificationService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineSpecificationRepository machineSpecificationRepository;

	/**
	 * Method used to save Machine Specification details 
	 * 
	 * @return IdResponseDto
	 * 		   Machine Specification ID which is successfully inserted
	 * 
	 * @param RequestDto<MachineSpecificationDto>
	 *             input from user Machine Specification DTO
	 * 
	 * @throws MasterDataServiceException
	 *             While inserting Machine Specification Detail If fails to insert  required Machine Specification
	 *             Detail
	 * 
	 */
	@Override
	public IdResponseDto createMachineSpecification(RequestDto<MachineSpecificationDto> machineSpecification) {

		MachineSpecification renMachineSpecification = new MachineSpecification();

		MachineSpecification entity = MetaDataUtils.setCreateMetaData(machineSpecification.getRequest(),
				MachineSpecification.class);
		try {
			renMachineSpecification = machineSpecificationRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorCode(),
					MachineSpecificationErrorCode.MACHINE_SPECIFICATION_INSERT_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}
		IdResponseDto idResponseDto = new IdResponseDto();
		MapperUtils.map(renMachineSpecification, idResponseDto);

		return idResponseDto;

	}

}
