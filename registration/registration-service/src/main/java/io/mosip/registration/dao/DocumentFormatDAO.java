package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DocumentFormat;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
public interface DocumentFormatDAO {

	/**
	 * This method fetches the document formats
	 * 
	 * @return {@link DocumentFormat} list of document formats
	 */

	List<DocumentFormat> getDocumentFormats();

}
