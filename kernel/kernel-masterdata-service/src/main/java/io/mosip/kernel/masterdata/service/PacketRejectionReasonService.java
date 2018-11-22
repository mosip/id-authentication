package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListRequestDto;
import io.mosip.kernel.masterdata.dto.ReasonListResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface PacketRejectionReasonService {
    public PostResponseDto saveReasonCategories(ReasonCategoryRequestDto reasonRequestDto);
    public ReasonListResponseDto saveReasonList(ReasonListRequestDto reasonRequestDto);
	public PacketRejectionReasonResponseDto getAllReasons();
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
