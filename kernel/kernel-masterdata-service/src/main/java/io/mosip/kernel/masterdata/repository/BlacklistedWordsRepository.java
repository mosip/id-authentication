package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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
	 * Method to fetch list of blacklisted words by language code
	 * 
	 * @param langCode
	 *            language code
	 * @return {@link List of BlacklistedWords }
	 */

	@Query("FROM BlacklistedWords blw WHERE blw.langCode = ?1 AND (blw.isDeleted IS NULL OR blw.isDeleted = false)")
	List<BlacklistedWords> findAllByLangCode(String langCode);

	/**
	 * Method to fetch all the blacklisted words
	 * 
	 * @return {@link List of BlacklistedWords }
	 */
	List<BlacklistedWords> findAllByIsDeletedFalseOrIsDeletedNull();

	/**
	 * Method to fetch word by word and langCode
	 * 
	 * @param word
	 *            word to fetch
	 * @param langCode
	 *            language code of the word
	 * @return word detail
	 */

	@Query("FROM BlacklistedWords blw WHERE lower(blw.word) = lower(?1) AND blw.langCode = ?2 AND (blw.isDeleted IS NULL OR blw.isDeleted = false)")
	BlacklistedWords findByWordAndLangCode(String word, String langCode);

	/**
	 * Method to delete the blacklisted word
	 * 
	 * @param word
	 *            input word to be deleted
	 * @param deletedDateTime
	 *            input deleted timeStamp
	 * @return no of rows deleted
	 */
	@Modifying
	@Transactional
	@Query("UPDATE BlacklistedWords bw SET bw.isDeleted = true , bw.deletedDateTime = ?2 WHERE lower(bw.word) = lower(?1) AND (bw.isDeleted IS NULL OR bw.isDeleted = false)")
	int deleteBlackListedWord(String word, LocalDateTime deletedDateTime);
}
