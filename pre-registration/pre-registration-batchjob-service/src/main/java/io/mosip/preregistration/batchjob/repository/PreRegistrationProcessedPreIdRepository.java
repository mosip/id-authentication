package io.mosip.preregistration.batchjob.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjob.entity.Processed_prereg_list;

@Repository("preRegProcessedRepository")
public interface PreRegistrationProcessedPreIdRepository extends BaseRepository<Processed_prereg_list, String>{
	
	//@Query("SELECT d FROM Processed_prereg_list d WHERE d.first_received_dtimes= :dtime")
	//List<Processed_prereg_list> findByfirst_received_dtimes(@Param("dtime") Timestamp dtime );
	
	//List<Processed_prereg_list> findBystatusCode(@Param("status_code") String status_code);

	List<Processed_prereg_list> findByisNew(@Param("isNew") boolean isNew);
	
}
