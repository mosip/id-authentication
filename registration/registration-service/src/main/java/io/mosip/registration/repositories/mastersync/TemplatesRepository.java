package io.mosip.registration.repositories.mastersync;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.Templates;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
@Repository
public interface TemplatesRepository extends BaseRepository<Templates, String> {

}
