package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
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

}
