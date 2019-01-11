package io.mosip.registration.repositories.mastersync;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterBlacklistedWords;

/**
 * Repository for Blacklisted words.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface MasterSyncBlacklistedWordsRepository extends BaseRepository<MasterBlacklistedWords, String> {

}
