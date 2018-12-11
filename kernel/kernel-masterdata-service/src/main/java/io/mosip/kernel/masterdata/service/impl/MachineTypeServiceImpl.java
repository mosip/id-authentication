package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.MachineTypeErrorCode;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.service.MachineTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to save  a Machine Type Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class MachineTypeServiceImpl implements MachineTypeService {

	/**
	 * Field to hold Machine Repository object
	 */
	@Autowired
	MachineTypeRepository machineTypeRepository;
	
	/**
	 * Method used to save Machine Type details 
	 * 
	 * @return CodeAndLanguageCodeID
	 * 		   Machine type code and language code which is successfully inserted
	 * 
	 * @param RequestDto
	 *             input from user Machine type DTO
	 * 
	 * @throws MasterDataServiceException
	 *             While inserting Machine type Detail If fails to insert  required Machine type
	 *             Detail
	 * 
	 */

	@Override
	public CodeAndLanguageCodeID createMachineType(RequestDto<MachineTypeDto> machineType) {
		MachineType renMachineType = null;

		MachineType entity = MetaDataUtils.setCreateMetaData(machineType.getRequest(), MachineType.class);

		try {
			renMachineType = machineTypeRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					MachineTypeErrorCode.MACHINE_TYPE_INSERT_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(renMachineType, codeLangCodeId);
		return codeLangCodeId;
	}

}
