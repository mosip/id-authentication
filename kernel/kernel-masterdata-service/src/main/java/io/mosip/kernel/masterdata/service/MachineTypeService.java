package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface provides methods to do CRUD operations on MachineType.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface MachineTypeService {

	/**
	 * Abstract method to save Machine Type Details to the Database
	 * 
	 * @param machineType
	 *            machineType DTO
	 * 
	 * @return CodeAndLanguageCodeID returning code and language code
	 *         {@link CodeAndLanguageCodeID}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine Type
	 */
	public CodeAndLanguageCodeID createMachineType(RequestDto<MachineTypeDto> machineType);

}
