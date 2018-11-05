package io.mosip.kernel.ridgenerator.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.ridgenerator.entity.Rid;

/**
 * Rid Generator repository.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface RidRepository extends BaseRepository<Rid, String> {

}
