package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
public interface BlacklistedWordsService {

	BlacklistedWordsResponseDto getAllBlacklistedWordsBylangCode(String langCode);
}
