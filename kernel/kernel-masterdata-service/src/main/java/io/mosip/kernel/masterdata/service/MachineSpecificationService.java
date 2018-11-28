package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.DeviceTypeCodeAndLanguageCodeAndId;
import io.mosip.kernel.masterdata.dto.MachineSpecificationRequestDto;
import io.mosip.kernel.masterdata.dto.MachineTypeCodeAndLanguageCodeAndId;

/**
 * This interface has abstract methods to fetch and save Machine Specification
 * Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface MachineSpecificationService {
	
	/**
	 * Function to save Machine Specification Details to the Database
	 * 
	 * @param machineSpecification
	 * 
	 * @return {@link DeviceTypeCodeAndLanguageCodeAndId}
	 */
	public MachineTypeCodeAndLanguageCodeAndId saveMachineSpecification(MachineSpecificationRequestDto machineSpecification);


}
