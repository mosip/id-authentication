package io.mosip.authentication.service.repository;

import java.util.Optional;

import io.mosip.authentication.service.impl.indauth.service.demo.LocationEntity;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

public interface LocationRepository extends BaseRepository<LocationEntity, Integer> {

	public Optional<LocationEntity> findByCodeAndStatus(String code, boolean status);
}
