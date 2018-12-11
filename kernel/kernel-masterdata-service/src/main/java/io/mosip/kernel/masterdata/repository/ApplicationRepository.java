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
	public List<Application> findAllByIsDeletedFalse(Class<Application> entityClass);
	
	/**
	 * Get all Application types of a specific language
	 * using language code
	 * 
	 * @param langCode -language code
	 * @return {@link List<Application>}
	 */
	public List<Application> findAllByLangCodeAndIsDeletedFalse(String languageCode);
	
	/**
	 * Get Application type by specific id and language code
	 * 
	 * @param code   -code
	 * @param langCode - language code
	 * @return {@link Application}
	 */
	public Application findByCodeAndLangCodeAndIsDeletedFalse(String code, String languageCode);
	
}
