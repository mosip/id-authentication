package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Application;

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
	public List<Application> findAllByIsActiveTrueAndIsDeletedFalse(Class<Application> entityClass);
	
	/**
	 * Get all Application types of a specific language
	 * using language code
	 * 
	 * @param langCode
	 * @return {@link List<Application>}
	 */
	public List<Application> findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(String languageCode);
	
	/**
	 * Get Application type by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain Application}
	 */
	public Application findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(String code, String languageCode);
	
}
