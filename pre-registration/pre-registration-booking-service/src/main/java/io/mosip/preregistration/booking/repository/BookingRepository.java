package io.mosip.preregistration.booking.repository;

import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
/**
 * Registration Repository
 * 
 * @author M1037462
 *
 */
@Repository("BookingRepository")
@Transactional
public interface BookingRepository extends BaseRepository<AvailibityEntity, String> {
	public static final String distDate = "SELECT DISTINCT availability_date  FROM prereg.reg_available_slot where regcntr_id=:regcntrId order by availability_date ASC";


	public List<AvailibityEntity> findByRegcntrIdAndRegDate(String regcntrId, String regDate);
	
	@Query(value = distDate, nativeQuery = true)
	public List<String> findDate(@Param("regcntrId") String regcntrId);
	

}
