package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.BlacklistedWords;

/**
 * Repository for Blacklisted words.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface BlacklistedWordsRepository extends BaseRepository<BlacklistedWords, String> {
	List<BlacklistedWords> findBlackListedWordsByIsActiveTrueAndLangCode(String langCode);
}
