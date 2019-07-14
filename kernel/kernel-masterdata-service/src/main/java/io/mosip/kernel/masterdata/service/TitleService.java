package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.TitleExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * Service class to fetch titles from master db
 * 
 * @author Sidhant Agarwal
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public interface TitleService {

	/**
	 * Method to get all titles from master db
	 * 
	 * @return list of all titles present in master db
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	TitleResponseDto getAllTitles();

	/**
	 * Method to get all titles for a particular language code
	 * 
	 * @param languageCode
	 *            input from user language code
	 * @return list of all titles for a particular language code
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 * @throws DataNotFoundException
	 *             when data not found
	 */
	TitleResponseDto getByLanguageCode(String languageCode);

	/**
	 * Service class to add a new title data
	 * 
	 * @param titleRequestDto
	 *            input from user
	 * @return primary key of inserted data
	 * @throws MasterDataServiceException
	 *             when entered data not created
	 */
	public CodeAndLanguageCodeID saveTitle(TitleDto titleRequestDto);

	/**
	 * Service class to update title data
	 * 
	 * @param titles
	 *            input from user
	 * @return composite primary key of updated row of data
	 * @throws RequestException
	 *             when data not found
	 * 
	 * @throws MasterDataServiceException
	 *             when data not updated successfully
	 */
	public CodeAndLanguageCodeID updateTitle(TitleDto titles);

	/**
	 * Service class to delete title data
	 * 
	 * @param code
	 *            input from user
	 * @return composite key of deleted row of data
	 * @throws RequestException
	 *             when data not found
	 * 
	 * @throws MasterDataServiceException
	 *             when data not deleted successfully
	 */
	public CodeResponseDto deleteTitle(String code);

	/**
	 * Method to get all titles
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
	 * 
	 * @return list of titles
	 * 
	 * @throws MasterDataServiceException
	 *             when data not fetched from DB
	 */
	PageDto<TitleExtnDto> getTitles(int pageNumber, int pageSize, String sortBy, String orderBy);
	
	/**
	 * Search titles.
	 *
	 * @param searchDto the search dto
	 * @return {@link PageResponseDto} the page response dto
	 */
	PageResponseDto<TitleExtnDto> searchTitles(SearchDto searchDto);
	
	/**
	 * Filter titles.
	 *
	 * @param filterValueDto the filter value dto
	 * @return the filter response dto
	 */
	FilterResponseDto filterTitles(FilterValueDto filterValueDto);
	
	

}
