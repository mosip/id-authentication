package io.mosip.kernel.masterdata.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;

/**
 * Repository for Blacklisted words.
 * 
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
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
