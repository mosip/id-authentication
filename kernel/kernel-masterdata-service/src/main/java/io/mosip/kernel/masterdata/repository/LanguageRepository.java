package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Language;

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
	public List<Language> findAllByIsDeletedFalseOrIsDeletedIsNull();

}
