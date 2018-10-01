package org.mosip.kernel.vidgenerator.repository;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.kernel.vidgenerator.entity.Vid;
import org.springframework.stereotype.Repository;

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
