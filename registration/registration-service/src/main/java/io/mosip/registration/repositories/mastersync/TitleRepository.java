package io.mosip.registration.repositories.mastersync;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.Title;

/**
 * Repository class for inserting titles into db
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@Repository
public interface TitleRepository extends BaseRepository<Title, String> {

}
