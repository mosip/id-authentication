package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Template;

/**
 * @author Neha
 * @since 1.0.0
 * 
 */
@Repository
public interface TemplateRepository extends BaseRepository<Template, String> {

	/**
	 * Get all Template types
	 *
	 * @param entityClass
	 *            the entity class type
	 * @return All the {@link Template}
	 */
	List<Template> findAllByIsDeletedFalseOrIsDeletedIsNull(Class<Template> entityClass);

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param code
	 *            the code
	 * @return All the {@link Template}
	 */
	@Query("FROM Template WHERE code =?1 AND (isDeleted is null OR isDeleted = false)")
	List<Template> findAllByCodeAndIsDeletedFalseOrIsDeletedIsNull(String code);

	/**
	 * To fetch all the {@link Template} based on language file format code
	 * 
	 * @param fileFormatCode
	 *            format code the file format code
	 * @return All the {@link Template}
	 */
	@Query("FROM Template WHERE fileFormatCode =?1 AND (isDeleted is null OR isDeleted = false)")
	List<Template> findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(String fileFormatCode);

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return All the {@link Template}
	 */
	@Query("FROM Template WHERE langCode =?1 AND (isDeleted is null OR isDeleted = false)")
	List<Template> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type
	 * code
	 * 
	 * @param langCode
	 *            the language code
	 * @param templateTypeCode
	 *            the template type code
	 * @return All the {@link Template}
	 */
	@Query("FROM Template WHERE langCode =?1 AND templateTypeCode =?2 AND (isDeleted is null OR isDeleted = false)")
	List<Template> findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode,
			String templateTypeCode);

	/**
	 * To fetch the template by id
	 * 
	 * @param id
	 * 		the id of template
	 * @return {@link Template}
	 */
	@Query("FROM Template WHERE id =?1 AND (isDeleted is null OR isDeleted = false)")
	Template findTemplateByIDAndIsDeletedFalseOrIsDeletedIsNull(String id);
}
