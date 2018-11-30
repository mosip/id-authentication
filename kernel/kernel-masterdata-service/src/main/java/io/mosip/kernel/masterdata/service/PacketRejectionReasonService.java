package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.ReasonListResponseDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface PacketRejectionReasonService {
    public PostResponseDto createReasonCategories(RequestDto<List<PostReasonCategoryDto>> reasonRequestDto);
    public ReasonListResponseDto createReasonList(RequestDto<List<ReasonListDto>> reasonRequestDto);
	public PacketRejectionReasonResponseDto getAllReasons();
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
