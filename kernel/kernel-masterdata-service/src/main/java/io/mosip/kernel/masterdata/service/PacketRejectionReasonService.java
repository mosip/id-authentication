package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PostReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.entity.id.CodeLangCodeAndRsnCatCodeID;

/**
 * 
 * @author Srinivasan
 *
 */
public interface PacketRejectionReasonService {
	/**
	 * 
	 * @param reasonRequestDto- reason reqest dto
	 * @return codeAndLanguageCodeId - composite key
	 */
    public CodeAndLanguageCodeID createReasonCategories(RequestDto<PostReasonCategoryDto> reasonRequestDto);
    /**
     * 
     * @param reasonRequestDto - reason reqest dto
     * @return codeLangCodeAndRsnCatCode - composite key
     */
    public CodeLangCodeAndRsnCatCodeID createReasonList(RequestDto<ReasonListDto> reasonRequestDto);
    
    /**
     * 
     * @return PacketRejectionReasonResponseDto
     */
	public PacketRejectionReasonResponseDto getAllReasons();
	
	/**
	 * 
	 * @param categoryCode - category code
	 * @param langCode - language code
	 * @return PacketRejectionReasonResponseDto
	 */
	public PacketRejectionReasonResponseDto getReasonsBasedOnLangCodeAndCategoryCode(String categoryCode,String langCode);
}
