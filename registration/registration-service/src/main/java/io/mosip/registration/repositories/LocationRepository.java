package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.entity.Location;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Repository
public interface LocationRepository extends BaseRepository<Location, GenericId> {

}
