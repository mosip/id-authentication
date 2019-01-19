package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DocumentCategory;

/**
 * 
 * @author Brahmnanda Reddy
 *
 */
public interface DocumentCategoryDAO {
	/**
	 * this method fetches the document categories
	 * 
	 * @return {@link List} of document categories
	 */

	List<DocumentCategory> getDocumentCategories();

}
