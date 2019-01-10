/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.entity.AvailibityEntity;

/**
 * This repository interface is used to define the JPA methods for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Repository("bookingAvailabilityRepository")
@Transactional
public interface BookingAvailabilityRepository extends BaseRepository<AvailibityEntity, String> {
	

	/**
	 * @param Registration center id
	 * @param Registration date
	 * @return List AvailibityEntity based registration id and registration date.
	 */
	public List<AvailibityEntity> findByRegcntrIdAndRegDateOrderByFromTimeAsc(String regcntrId, LocalDate regDate);

	/**
	 * @param regcntrId
	 * @param fromDate
	 * @param toDate
	 * @return List of LocalDate based on date
	 */
	public List<LocalDate> findDate(@Param("regcntrId") String regcntrId, @Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	/**
	 * @param slot_from_time
	 * @param slot_to_time
	 * @param reg_date
	 * @param regcntr_id
	 * @return Availability entity
	 */
	public AvailibityEntity findByFromTimeAndToTimeAndRegDateAndRegcntrId(
			@Param("slot_from_time") LocalTime slotFromTime, @Param("slot_to_time") LocalTime slotToTime,
			@Param("availability_date") LocalDate regDate, @Param("regcntr_id") String regcntrd);

}
