package io.mosip.kernel.masterdata.repository;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */
public interface BlacklistedWordsRepository extends BaseRepository<BlacklistedWords, String> {

	List<BlacklistedWords> findAllByLangCode(String langCode);
}
