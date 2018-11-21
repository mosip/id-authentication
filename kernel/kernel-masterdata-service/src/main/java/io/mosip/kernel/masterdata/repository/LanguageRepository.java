package io.mosip.kernel.masterdata.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Language;

/**
 * Repository function to fetching language details
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */

public interface LanguageRepository extends BaseRepository<Language, String> {

	public List<Language> findAllByIsActiveTrueAndIsDeletedFalse();

}
