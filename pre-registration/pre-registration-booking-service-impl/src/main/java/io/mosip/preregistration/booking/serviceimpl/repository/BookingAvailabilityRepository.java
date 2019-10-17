/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.serviceimpl.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.booking.serviceimpl.entity.AvailibityEntity;

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
	
	/**
	 * 
	 * @param regDate
	 * @return list of available date
	 */
	@Query("SELECT DISTINCT e.regDate FROM AvailibityEntity e  WHERE e.regDate>= ?1")
	public List<LocalDate> findAvaialableDate(LocalDate regDate);
	
	public List<AvailibityEntity> findByRegcntrId(String regCenterId);
	
	/**
	 * 
	 * @param regDate
	 * @return list of available date
	 */
	@Query("SELECT DISTINCT e.regcntrId FROM AvailibityEntity e  WHERE e.regDate>= ?1")
	public List<String> findAvaialableRegCenter(LocalDate regDate);
	
	/**
	 * 
	 * @param regDate
	 * @param regcntrId
	 * @return list of available date
	 */
	@Query("SELECT DISTINCT e.regDate FROM AvailibityEntity e  WHERE e.regDate>= ?1 and e.regcntrId=?2")
	public List<LocalDate> findAvaialableDate(LocalDate regDate, String regcntrId);
	
	
	/**
	 * 
	 * @param regDate
	 * @param regcntrId
	 * @return list of AvailibityEntity
	 */
	@Query("SELECT DISTINCT e FROM AvailibityEntity e  WHERE e.regDate= ?1 and e.regcntrId=?2")
	public List<AvailibityEntity> findAvaialableSlots(LocalDate regDate, String regcntrId);
	
	
	/**
	 * 
	 * @param regDate
	 * @param regcntrId
	 * @return deleted number of slots
	 */
	public int deleteByRegcntrIdAndRegDate( String regcntrId ,LocalDate regDate);
	
	/**
	 * 
	 * @param regDate
	 * @return list of regcntrId
	 */
	public int deleteByRegcntrIdAndRegDateGreaterThanEqual(String regcntrId, LocalDate regDate);
	
	/**
	 * @param Registration center id
	 * @param Registration startDate
	 * @param Registration endDate
	 * @return List AvailibityEntity based registration id and registration date.
	 */
	public List<AvailibityEntity> findByRegcntrIdAndRegDateGreaterThanEqualAndRegDateLessThanEqualOrderByFromTimeAsc(String regcntrId, LocalDate starteDate,LocalDate endDate);


}
