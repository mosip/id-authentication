package io.mosip.registration.service.impl;

import java.util.List;

import io.mosip.registration.entity.DocumentCategory;

/**
 * 
 * @author balamurugamn.ramamoorthy
 *
 */
public interface DocumentCategoryService {

	/**
	 * 
	 * This method is used to fetch all the document categories
	 * 
	 * @return List<DocumentCategory>
	 */
	List<DocumentCategory> getDocumentCategories();

	/**
	 * 
	 * This method is used to fetch all the document categories for a specific
	 * langcode
	 * 
	 * @return List<DocumentCategory>
	 */
	List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode);

}