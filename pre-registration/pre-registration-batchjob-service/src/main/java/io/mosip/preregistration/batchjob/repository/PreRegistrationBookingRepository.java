package io.mosip.preregistration.batchjob.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjob.entity.AvailibityEntity;


@Repository("bookingRepository")
@Transactional
public interface PreRegistrationBookingRepository extends BaseRepository<AvailibityEntity, String> {
	public static final String distDate = "SELECT DISTINCT reg_date  FROM prereg.availability where regcntr_id=:regcntrId";


	public List<AvailibityEntity> findByRegcntrIdAndRegDate(String regcntrId, String regDate);
	
	@Query(value = distDate, nativeQuery = true)
	public List<String> findDate(@Param("regcntrId") String regcntrId);
	

}
