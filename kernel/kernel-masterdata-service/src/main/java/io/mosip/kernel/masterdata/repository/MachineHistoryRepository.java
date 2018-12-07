package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MachineHistory;

/**
 * Repository function to fetching machine History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
@Transactional
public interface MachineHistoryRepository extends BaseRepository<MachineHistory, String> {
	


	/**
	 * This method trigger query to fetch Machine History Details based on
	 * Machine Id, language code and effective date time
	 * 
	 * @param id
	 *            Machine History id provided by user
	 * @param langCode
	 *            language code provided by user
	 * @param effectDtimes
	 *            effective Date and time provided by user in the format "yyyy-mm-ddThh:mm:ss"
	 * @return List<MachineHistory> fetched from database
	 */
	
  
	@Query(value = "Select 	m.eff_dtimes, m.id,m.cr_by, m.cr_dtimes, m.del_dtimes, m.is_active, m.is_deleted, m.upd_by, m.upd_dtimes, m.ip_address, m.lang_code, m.mac_address, m.mspec_id, m.name,  m.serial_num, m.validity_end_dtimes from master.machine_master_h m where m.id = ?1 and m.lang_code = ?2 and m.eff_dtimes <= ?3 and ( m.is_deleted = false or m.is_deleted is null) order by m.eff_dtimes desc limit 1", nativeQuery = true)
	//EntityManager em = .entityManager();
	
	//TypedQuery<MachineHistory> query = em.createQuery("Select 	m.eff_dtimes, m.id,m.cr_by, m.cr_dtimes, m.del_dtimes, m.is_active, m.is_deleted, m.upd_by, m.upd_dtimes, m.ip_address, m.lang_code, m.mac_address, m.mspec_id, m.name,  m.serial_num, m.validity_end_dtimes from master.machine_master_h m where m.id = ?1 and m.lang_code = ?2 and m.eff_dtimes <= ?3 and ( m.is_deleted = false or m.is_deleted is null) order by m.eff_dtimes desc");
	List<MachineHistory> findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(String id, String langCode,
			LocalDateTime effectDtimes);
}
