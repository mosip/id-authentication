package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface has abstract methods to save a Machine Type Details to the
 * database table
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
	 * 			input from user
	 * 
	 * @return CodeAndLanguageCodeID
	 * 				returning code and language code {@link CodeAndLanguageCodeID}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine Type 
	 */
   public CodeAndLanguageCodeID createMachineType(RequestDto<MachineTypeDto> machineType);

}
