package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Language;

/**
 * Repository to perform CRUD operations on Language.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface LanguageRepository extends BaseRepository<Language, String> {

	
		List<Language> findAllByIsActiveTrue();
}
