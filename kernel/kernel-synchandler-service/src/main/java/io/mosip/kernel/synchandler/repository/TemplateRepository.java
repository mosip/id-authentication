package io.mosip.kernel.synchandler.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.Template;

/**
 * 
 * @author Neha
 * @since 1.0.0
 * 
 */
@Repository
public interface TemplateRepository extends BaseRepository<Template, String> {

	/**
	 * Get all Biometric types
	 *
	 * @return {@link List<BiometricType>}
	 */
	public List<Template> findAllByIsDeletedFalse(Class<Template> entityClass);

	/**
	 * To fetch all the {@link Template} based on language code
	 * 
	 * @param languageCode
	 * @return {@link List<Template>}
	 */
	public List<Template> findAllByLanguageCodeAndIsDeletedFalse(String languageCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type
	 * code
	 * 
	 * @param languageCode
	 * @param templateTypeCode
	 * @return {@link List<Template>}
	 */
	public List<Template> findAllByLanguageCodeAndTemplateTypeCodeAndIsDeletedFalse(String languageCode,
			String templateTypeCode);

}
