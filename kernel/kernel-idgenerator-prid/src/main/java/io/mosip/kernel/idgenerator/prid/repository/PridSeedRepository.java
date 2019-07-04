package io.mosip.kernel.idgenerator.prid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.prid.entity.PridSeed;

/**
 * Repository for seed number of prid generation algorithm.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface PridSeedRepository extends BaseRepository<PridSeed, String> {

}
