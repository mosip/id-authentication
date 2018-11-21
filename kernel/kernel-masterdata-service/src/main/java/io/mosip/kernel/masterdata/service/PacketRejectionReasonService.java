package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PacketRejectionReasonRequestDto;
import io.mosip.kernel.masterdata.dto.PacketRejectionReasonResponseDto;

/**
 * 
 * @author Srinivasan
 *
 */
public interface PacketRejectionReasonService {
    public PacketRejectionReasonResponseDto saveReasonCategories(PacketRejectionReasonRequestDto reasonRequestDto);
    public PacketRejectionReasonResponseDto saveReasonList(PacketRejectionReasonRequestDto reasonRequestDto);
	public PacketRejectionReasonResponseDto getAllReasons();
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
