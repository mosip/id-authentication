package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.IdTypeRequestDto;
import io.mosip.kernel.masterdata.dto.IdTypeResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;

/**
 * Interface that provides the method for id types operations.
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

	/**
	 * This method adds the idtypes.
	 * 
	 * @param idTypeRequestDto
	 *            the request dto that holds the idtypes to be added.
	 * @return the idtypes added.
	 */
	PostResponseDto addIdType(IdTypeRequestDto idTypeRequestDto);
}
