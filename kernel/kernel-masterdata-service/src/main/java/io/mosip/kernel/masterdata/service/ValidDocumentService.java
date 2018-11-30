package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.ValidDocumentRequestDto;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;

/**
 * This interface contains methods to create valid document.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface ValidDocumentService {
	/**
	 * This method create valid document in table.
	 * 
	 * @param document
	 *            the dto.
	 * @return {@link ValidDocumentID}
	 */
	public ValidDocumentID insertValidDocument(ValidDocumentRequestDto document);
}
