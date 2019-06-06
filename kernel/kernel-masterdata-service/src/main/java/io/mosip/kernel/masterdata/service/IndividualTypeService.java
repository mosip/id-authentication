package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.IndividualTypeExtnDto;

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

	/**
	 * This method provides with all individual type.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the individual types
	 */
	public PageDto<IndividualTypeExtnDto> getIndividualTypes(int pageNumber, int pageSize, String sortBy,
			String orderBy);

}
