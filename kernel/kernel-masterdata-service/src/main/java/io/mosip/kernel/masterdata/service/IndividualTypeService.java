package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;

/**
 * This class provides operation related to Individual type.
 * 
 * @author Bal Vikash Sharma
 *
 */
public interface IndividualTypeService {

	/**
	 * This method returns all the individual type which is active.
	 * 
	 * @return all active individual type in database.
	 */
	public IndividualTypeResponseDto getAllIndividualTypes();

}
