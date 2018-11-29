package io.mosip.kernel.synchandler.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.BlacklistedWords;

/**
 * repository for blacklisted words
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
public interface BlacklistedWordsRepository extends BaseRepository<BlacklistedWords, String> {

	/**
	 * method to fetch list of blacklisted words by language code
	 * 
	 * @param langCode
	 * @return {@link List of BlacklistedWords }
	 */
	List<BlacklistedWords> findAllByLangCode(String langCode);

	/**
	 * method to fetch all the blacklisted words
	 * 
	 * @return {@link List of BlacklistedWords }
	 */
	List<BlacklistedWords> findAllByIsDeletedFalseOrIsDeletedNull();
}
