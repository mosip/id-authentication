package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Application;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface ApplicationRepository extends BaseRepository<Application, String> {

	/**
	 * Get all Application types
	 * 
	 * @param entityClass
	 *            of type {@link Application}
	 * @return list of {@link Application}
	 */
	List<Application> findAllByIsDeletedFalseOrIsDeletedNull(Class<Application> entityClass);

	/**
	 * 
	 * Get all Application types of a specific language using language code
	 * 
	 * @param languageCode
	 *            of type {@link String}
	 * @return list of {@link Application}
	 */
	@Query("FROM Application WHERE langCode =?1 AND (isDeleted is null OR isDeleted = false)")
	List<Application> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String languageCode);

	/**
	 * Get Application type by specific id and language code
	 * 
	 * @param code
	 *            -code
	 * @param languageCode
	 *            - language code
	 * @return {@link Application}
	 */
	@Query("FROM Application WHERE code =?1 AND langCode =?2 AND (isDeleted is null OR isDeleted = false)")
	Application findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String code, String languageCode);

}
