package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DocumentType;

/**
 * Handles the Doc types related details
 * 
 * @author Brahmanada Reddy
 *
 */
public interface DocumentTypeDAO {

	/**
	 * This method fetches the document types
	 * 
	 * @return {@link List} of document types
	 */
	List<DocumentType> getDocumentTypes();

	/**
	 * fetches all the document types by the given doc type name
	 * 
	 * @param docTypeName
	 *            - Doc Type Name
	 * @return List - list of fetched doc types
	 */
	List<DocumentType> getDocTypeByName(String docTypeName);

}
