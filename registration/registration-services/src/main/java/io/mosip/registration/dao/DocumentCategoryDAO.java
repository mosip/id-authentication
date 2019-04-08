package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DocumentCategory;

/**
 * Handles the Document categories related functions
 * 
 * @author Brahmnanda Reddy
 *
 */
public interface DocumentCategoryDAO {

	/**
	 * this method fetches the document categories
	 * 
	 * @return List - list of document categories
	 */
	List<DocumentCategory> getDocumentCategories();

	/**
	 * Fetches all doc categories by lang code
	 * 
	 * @param langCode
	 *            - lamnguage code
	 * @return List - list of doc categories
	 */
	List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode);

}
