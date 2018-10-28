package io.mosip.kernel.vidgenerator.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.vidgenerator.entity.Vid;

/**
 * Repository for VidGenerator
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
@Repository
public interface VidRepository extends BaseRepository<Vid, String> {
}
