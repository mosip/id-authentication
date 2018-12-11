package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Language;

/**
 * Repository to perform CRUD operations on Language.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see Language
 * @see BaseRepository
 *
 */
@Repository
public interface LanguageRepository extends BaseRepository<Language, String> {

	/**
	 * This method provides all the languages having <b>isActive</b> is <b>true</b>
	 * and <b>isDeleted</b> is <b>false</b> present in MOSIP system.
	 * 
	 * @see Language
	 * @return List<Language>
	 */
	public List<Language> findAllByIsDeletedFalse();
	@Query("FROM Language WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<Language> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
