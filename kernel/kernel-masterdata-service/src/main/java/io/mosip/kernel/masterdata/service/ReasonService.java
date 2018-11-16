package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.ReasonResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface ReasonService {
    public ReasonResponseDto saveOrUpdateReasons(ReasonResponseDto reasonRequestDto);
	public ReasonResponseDto getAllReasons();
	public ReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
