package org.mosip.auth.service.dao;

import org.mosip.auth.service.entity.VIDEntity;
import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * The Interface VIDRepository is used to fetch VIDEntity.
 */
@Repository
public interface VIDRepository extends BaseRepository<VIDEntity, String> {
}
