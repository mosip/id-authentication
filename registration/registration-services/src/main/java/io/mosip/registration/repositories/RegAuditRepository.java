package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository interface for {@link Audit} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */

public interface RegAuditRepository extends BaseRepository<Audit, Long> {

	/**
	 * Retrieves the {@link Audit} which are yet to be synchronized to the server
	 * 
	 * @return the list of unsynchronized {@link Audit}
	 */
	@Query(value = "SELECT * FROM AUDIT.APP_AUDIT_LOG WHERE IS_SYNC IS NULL", nativeQuery = true)
	List<Audit> findAllUnsyncAudits();

	/**
	 * Updates the {@link Audit} that are synchronized to the server
	 * 
	 * @param auditUUIDs
	 *            the list of UUID of the {@link Audit} to be updated
	 * @return returns the number of records updated
	 */
	@Modifying
	@Query(value = "UPDATE AUDIT.APP_AUDIT_LOG a SET a.IS_SYNC = true WHERE a.LOG_ID IN :audits", nativeQuery = true)
	int updateSyncAudits(@Param("audits") List<String> auditUUIDs);
	
	void deleteAllInBatchBycreatedAtBetween(LocalDateTime auditLogFromDtimes,LocalDateTime auditLogToDtimes);
}
