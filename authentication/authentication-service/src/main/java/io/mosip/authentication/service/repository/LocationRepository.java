package io.mosip.authentication.service.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.impl.indauth.service.demo.LocationEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * 
 * @author Dinesh Karuppiah
 */

@Repository
public interface LocationRepository extends BaseRepository<LocationEntity, Integer> {

	public Optional<LocationEntity> findByCodeAndIsActive(String code, boolean status);

}
