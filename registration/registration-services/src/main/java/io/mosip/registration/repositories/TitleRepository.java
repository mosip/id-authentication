package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Title;

/**
 * Repository class for fetching titles from master db
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface TitleRepository extends BaseRepository<Title, String> {
	

}
