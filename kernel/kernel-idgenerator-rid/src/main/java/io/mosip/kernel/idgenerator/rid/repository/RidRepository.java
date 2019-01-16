package io.mosip.kernel.idgenerator.rid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.rid.entity.Rid;

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
