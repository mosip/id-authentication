package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.RegistrationCenter;

/**
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterRepository extends JpaRepository<RegistrationCenter, String> {

	/**
	 * Method to fetch the registration center for which machine is mapped.
	 * 
	 * @param machineid id of the machine
	 * @return {@link RegistrationCenter} - list of registration center
	 */
	@Query(value = "SELECT distinct r.id, r.name, r.cntrtyp_code, r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.center_end_time,r.center_start_time,r.time_zone,r.contact_person,r.lunch_start_time,r.lunch_end_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.is_active, r.cr_by,r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes,r.lang_code from  master.registration_center r , master.reg_center_machine rcmd where r.id=rcmd.regcntr_id and rcmd.machine_id= ?1", nativeQuery = true)
	List<RegistrationCenter> findRegistrationCenterByMachineId(String machineid);

	/**
	 * Method to fetch the latest registration center for which machine is mapped.
	 * 
	 * @param machineid        id of the machine
	 * @param lastUpdated      timeStamp - last updated time
	 * @param currentTimeStamp - current timestamp
	 * @return list of {@link RegistrationCenter} - list of registration center
	 */
	@Query(value = "SELECT distinct r.id, r.name, r.cntrtyp_code,r.addr_line1, r.addr_line2, r.addr_line3,r.number_of_kiosks,r.per_kiosk_process_time,r.center_end_time,r.center_start_time,r.time_zone,r.contact_person,r.lunch_start_time,r.lunch_end_time,r.latitude, r.longitude, r.location_code,r.holiday_loc_code,r.contact_phone, r.working_hours, r.lang_code,r.is_active, r.cr_by,r.cr_dtimes, r.upd_by,r.upd_dtimes, r.is_deleted, r.del_dtimes,r.lang_code from  master.registration_center r , master.reg_center_machine rcmd where r.id=rcmd.regcntr_id and rcmd.machine_id= ?1 and ((r.cr_dtimes > ?2 and r.cr_dtimes<=?3) or (r.upd_dtimes > ?2 and r.upd_dtimes <=?3) or (r.del_dtimes > ?2 and r.del_dtimes <=?3))", nativeQuery = true)
	List<RegistrationCenter> findLatestRegistrationCenterByMachineId(String machineid, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);

	/**
	 * 
	 * @param regCenterId - registration center id
	 * 
	 * @return list of {@link RegistrationCenter} - list of registration center
	 */
	List<RegistrationCenter> findRegistrationCenterByIdAndIsActiveIsTrue(String regCenterId);
}
