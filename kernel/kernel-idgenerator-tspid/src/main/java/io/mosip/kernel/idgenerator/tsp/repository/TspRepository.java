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
	
	@Query(value = "select t.tsp_id,t.cr_dtimes,t.upd_by,t.upd_dtimes,t.is_deleted,t.cr_by,t.del_dtimes FROM ids.tspid t where t.tsp_id = (select max(t.tsp_id) from ids.tspid t) ", nativeQuery = true)
	Tsp findMaxTspId();
}
