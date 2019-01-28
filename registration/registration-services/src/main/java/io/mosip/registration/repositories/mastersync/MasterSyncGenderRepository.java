package io.mosip.registration.repositories.mastersync;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterGender;

/**
 * Repository class for fetching gender data
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncGenderRepository extends BaseRepository<MasterGender, String> {

	List<MasterGender> findByLangCode(String langCode);

}
