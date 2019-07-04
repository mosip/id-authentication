package io.mosip.registration.service.doc.category;

import java.util.List;

import io.mosip.registration.entity.DocumentCategory;

/**
 * Service class for Document Category
 * 
 * @author balamurugamn.ramamoorthy
 * @since 1.0.0
 */
public interface DocumentCategoryService {

	/**
	 * 
	 * This method is used to fetch all the document categories
	 * 
	 * @return List - list of doc categories
	 */
	List<DocumentCategory> getDocumentCategories();

	/**
	 * 
	 * This method is used to fetch all the document categories for a specific
	 * langcode
	 * 
	 * @param langCode
	 *            - language code
	 * @return List - list of doc categories
	 */
	List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode);

}