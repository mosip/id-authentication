package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.IdTypeRequestDto;
import io.mosip.kernel.masterdata.dto.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;

/**
 * Interface that provides the method for fetching id types based on language
 * code.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface IdTypeService {
	/**
	 * This method returns the id type response dto.
	 * 
	 * @param languageCode
	 *            the language code.
	 * @return the response dto.
	 */
	IdTypeResponseDto getIdTypeByLanguageCode(String languageCode);

	PostResponseDto addIdType(IdTypeRequestDto idTypeRequestDto);
}
