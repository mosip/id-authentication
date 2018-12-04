package io.mosip.kernel.idgenerator.tsp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tsp.entity.Tsp;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface TspRepository extends BaseRepository<Tsp, String> {
	
	@Query(value = "select t.id,t.tsp_id,t.cr_dtimes FROM ids.tsp_id t where t.tsp_id = (select max(t.tsp_id) from ids.tsp_id t) ", nativeQuery = true)
	Tsp findMaxTspId();
}
