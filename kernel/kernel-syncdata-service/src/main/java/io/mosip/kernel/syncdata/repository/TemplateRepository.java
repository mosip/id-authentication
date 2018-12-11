package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Template;

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
	public List<Template> findAllByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * To fetch all the {@link Template} based on language code and template type
	 * code
	 * 
	 * @param languageCode
	 * @param templateTypeCode
	 * @return {@link List<Template>}
	 */
	public List<Template> findAllByLangCodeAndTemplateTypeCodeAndIsDeletedFalse(String langCode,
			String templateTypeCode);

	@Query("FROM Template WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Template> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);

	@Query(value = "select templ.id as,templ.cr_by,templ.cr_dtimes,templ.del_dtimes,templ.is_active,templ.is_deleted,templ.upd_by,templ.upd_dtimes,templ.descr,templ.file_format_code ,templ.file_txt,templ.lang_code,templ.model,templ.module_id,templ.module_name ,templ.name,templ.template_typ_code  from master.template templ", nativeQuery = true)
	List<Template> findAllTemplates();

	@Query(value = "select templ.id as,templ.cr_by,templ.cr_dtimes,templ.del_dtimes,templ.is_active,templ.is_deleted,templ.upd_by,templ.upd_dtimes,templ.descr,templ.file_format_code ,templ.file_txt,templ.lang_code,templ.model,templ.module_id,templ.module_name ,templ.name,templ.template_typ_code  from master.template templ and (templ.cr_dtimes > ?1)", nativeQuery = true)
	List<Template> findLatestTemplates(LocalDateTime lastUpdated);
}
