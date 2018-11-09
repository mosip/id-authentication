package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.entity.GlobalContextParamId;

/**
 * The repository interface for {@link GlobalContextParam} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface GlobalContextParamRepository extends BaseRepository<GlobalContextParam, GlobalContextParamId> {

	List<GlobalContextParam> findByNameIn(List<String> loginParams);
}
