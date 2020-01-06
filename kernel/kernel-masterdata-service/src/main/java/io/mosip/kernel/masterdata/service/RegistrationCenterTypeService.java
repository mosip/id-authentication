package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.RegistrationCenterTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseCodeDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Interface that provides methods for RegistrationCenterType operations.
 * 
 * @author Sagar Mahapatra
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface RegistrationCenterTypeService {
	/**
	 * Method to add registration center type.
	 * 
	 * @param registrationCenterTypeDto
	 *            the request dto {@link RegistrationCenterTypeDto}.
	 * @return the response {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID createRegistrationCenterType(RegistrationCenterTypeDto registrationCenterTypeDto);

	/**
	 * Method to update registration center type.
	 * 
	 * @param registrationCenterTypeDto
	 *            the request dto {@link RegistrationCenterTypeDto}.
	 * @return the response {@link CodeAndLanguageCodeID}.
	 */
	public CodeAndLanguageCodeID updateRegistrationCenterType(RegistrationCenterTypeDto registrationCenterTypeDto);

	/**
	 * Method to delete registration center type.
	 * 
	 * @param registrationCenterTypeCode
	 *            the code of the registration center type which needs to be
	 *            deleted.
	 * @return the response {@link CodeResponseDto}.
	 */
	public CodeResponseDto deleteRegistrationCenterType(String registrationCenterTypeCode);

	/**
	 * Method to fetch all the registration center types.
	 * 
	 * @param pageNumber
	 *            next page number to get the requested data
	 * 
	 * @param pageSize
	 *            number of data in the list
	 * @param sortBy
	 *            sorting data based the column name
	 * @param orderBy
	 *            order the list based on desc or asc
	 * @return page of registration center types.
	 */
	public PageDto<RegistrationCenterTypeExtnDto> getAllRegistrationCenterTypes(int pageNumber, int pageSize,
			String sortBy, String orderBy);

	/**
	 * Method that returns the column values of specific filter column name.
	 * 
	 * @param filterValueDto
	 *            the request DTO that provides the column name.
	 * @return the response containing the filter values.
	 */
	public FilterResponseCodeDto registrationCenterTypeFilterValues(FilterValueDto filterValueDto);

	/**
	 * Method to search and sort the filter based on the filters and sorting
	 * provided
	 * 
	 * @param dto
	 *            contains the data for searching and sorting.
	 * @return list of document type with all the metadata.
	 */
	public PageResponseDto<RegistrationCenterTypeExtnDto> searchRegistrationCenterTypes(SearchDto dto);
}
