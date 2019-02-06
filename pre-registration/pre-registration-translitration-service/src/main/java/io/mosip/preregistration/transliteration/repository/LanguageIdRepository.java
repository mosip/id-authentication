/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.transliteration.entity.LanguageIdEntity;

/**
 * This repository interface is used to define the JPA methods for Transliteration application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Repository
public interface LanguageIdRepository extends BaseRepository<LanguageIdEntity, String>{

	/**
	 * @param fromLang
	 * @param toLang
	 * @return the languageId for from language and to language.
	 */
	LanguageIdEntity findByFromLangAndToLang(String fromLang,String toLang);
}
