package io.mosip.kernel.idgenerator.vid.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.vid.entity.VidSeed;

/**
 * Repository for seed number in vid generation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface VidSeedRepository extends BaseRepository<VidSeed, String> {

}
