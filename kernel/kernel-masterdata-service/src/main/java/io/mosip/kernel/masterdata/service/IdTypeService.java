package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.IdTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.IdTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

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
	CodeAndLanguageCodeID createIdType(RequestDto<IdTypeDto> idTypeRequestDto);
}
