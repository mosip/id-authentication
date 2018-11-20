package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.ReasonRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface ReasonService {
    public ReasonResponseDto saveReasonCategories(ReasonRequestDto reasonRequestDto);
    public ReasonResponseDto saveReasonList(ReasonRequestDto reasonRequestDto);
	public ReasonResponseDto getAllReasons();
	public ReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
