package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Template;

/**
 * 
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
	public List<Template> findAllByIsDeletedFalse(Class<Template> entityClass);

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * @return All the {@link Template}
	 */
	public List<Template> findAllByLangCodeAndIsDeletedFalse(String langCode);

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
	public List<Template> findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(String langCode,
			String templateTypeCode);
}
