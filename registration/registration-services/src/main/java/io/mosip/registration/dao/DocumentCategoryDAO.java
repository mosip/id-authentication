package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DocumentCategory;

/**
 * This class is used to fetch all the Document category related data.
 * 
 * @author Brahmnanda Reddy
 *
 */
public interface DocumentCategoryDAO {

	/**
	 * This method is used to fetch the document categories
	 * 
	 * @return List of document categories
	 */
	List<DocumentCategory> getDocumentCategories();

	/**
	 * This method is used to fetch all document categories by language code.
	 * 
	 * @param langCode
	 *            Language code
	 * @return List of doc categories
	 */
	List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode);

}
