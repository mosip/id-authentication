package io.mosip.admin.masterdata.service;
/**
 * Masterdata card service
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */

import io.mosip.admin.masterdata.dto.MasterDataCardResponseDto;

public interface MasterDataCardService {

	/**
	 * This method will provide the masterdata cards based on the input languages.
	 * 
	 * @param languages
	 *            list of language
	 * @return {@link MasterDataCardResponseDto}
	 */
	public MasterDataCardResponseDto getMasterdataCards(String langCode);
}
