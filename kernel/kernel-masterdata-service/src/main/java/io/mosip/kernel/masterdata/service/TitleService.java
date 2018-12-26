package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Service class to fetch titles from master db
 * 
 * @author Sidhant Agarwal
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
	public CodeAndLanguageCodeID saveTitle(RequestDto<TitleDto> titleRequestDto);

	/**
	 * Service class to update title data
	 * 
	 * @param titles
	 *            input from user
	 * @return composite primary key of updated row of data
	 * @throws DataNotFoundException
	 *             when data not found
	 * @throws MasterDataServiceException
	 *             when data not updated successfully
	 */
	public CodeAndLanguageCodeID updateTitle(RequestDto<TitleDto> titles);

	/**
	 * Service class to delete title data
	 * 
	 * @param code
	 *            input from user
	 * @return composite key of deleted row of data
	 * @throws DataNotFoundException
	 *             when data not found
	 * @throws MasterDataServiceException
	 *             when data not deleted successfully
	 */
	public CodeResponseDto deleteTitle(String code);

}
