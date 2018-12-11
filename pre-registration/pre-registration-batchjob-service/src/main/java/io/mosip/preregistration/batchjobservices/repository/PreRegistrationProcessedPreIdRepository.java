package io.mosip.preregistration.batchjobservices.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjobservices.entity.Processed_Prereg_List;

/**
 * @author M1043008
 *
 */
@Repository("preRegProcessedRepository")
public interface PreRegistrationProcessedPreIdRepository extends BaseRepository<Processed_Prereg_List, String>{
	
	List<Processed_Prereg_List> findByisNew(@Param("isNew") boolean isNew);
	
}
