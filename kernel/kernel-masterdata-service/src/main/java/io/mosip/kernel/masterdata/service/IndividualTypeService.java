package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.IndividualTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.IndividualTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;

/**
 * This class provides operation related to Individual type.
 * 
 * @author Bal Vikash Sharma
 * @author Sidhant Agarwal
 * 
 * @since 1.0.0
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

	/**
	 * Method to search Individual Types.
	 * 
	 * @param dto
	 *            the searchDTO
	 * @return {@link PageResponseDto} containing pages of the searched values.
	 */
	public PageResponseDto<IndividualTypeExtnDto> searchIndividuals(SearchDto dto);

	/**
	 * Method that returns the column values of specific filter column name.
	 * 
	 * @param filterValueDto
	 *            the request DTO that provides the column name.
	 * @return the response containing the filter values.
	 */
	public FilterResponseDto individualsFilterValues(FilterValueDto filterValueDto);

}
