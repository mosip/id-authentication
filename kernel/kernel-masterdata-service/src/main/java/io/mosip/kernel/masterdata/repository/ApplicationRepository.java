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
	 * @param entityClass
	 *            of type {@link Application}
	 * @return list of {@link Application}
	 */
	public List<Application> findAllByIsDeletedFalseOrIsDeletedNull(Class<Application> entityClass);

	/**
	 * 
	 * Get all Application types of a specific language using language code
	 * 
	 * @param languageCode
	 *            of type {@link String}
	 * @return list of {@link Application}
	 */
	public List<Application> findAllByLangCodeAndIsDeletedFalseOrIsDeletedNull(String languageCode);

	/**
	 * Get Application type by specific id and language code
	 * 
	 * @param code
	 *            -code
	 * @param languageCode
	 *            - language code
	 * @return {@link Application}
	 */
	public Application findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedNull(String code, String languageCode);

}
