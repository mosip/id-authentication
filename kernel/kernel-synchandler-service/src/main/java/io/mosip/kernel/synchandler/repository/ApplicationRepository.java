package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.Application;

/**
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface ApplicationRepository extends BaseRepository<Application, String> {

	/**
	 * Get all Application types
	 * 
	 * @return {@link List<Application>}
	 */
	public List<Application> findAllByIsDeletedFalse(Class<Application> entityClass);

	/**
	 * Get all Application types of a specific language using language code
	 * 
	 * @param langCode
	 * @return {@link List<Application>}
	 */
	public List<Application> findAllByLangCodeAndIsDeletedFalse(String languageCode);

	/**
	 * Get Application type by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain Application}
	 */
	public Application findByCodeAndLangCodeAndIsDeletedFalse(String code, String languageCode);

	@Query("FROM Application WHERE createdtimes > ?1 or updatedtimes > ?1  or deletedtimes > ?1")
	List<Application> findAllNewUpdateDeletedApplication(LocalDateTime lastUpdated);
}
