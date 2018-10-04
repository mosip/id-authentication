package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

/**
 * The Interface VIDRepository is used to fetch VIDEntity.
 */
@Repository
public interface VIDRepository extends BaseRepository<VIDEntity, String> {
}
