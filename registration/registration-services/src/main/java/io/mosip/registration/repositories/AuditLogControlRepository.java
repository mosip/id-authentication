package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.RegistrationAuditDates;

/**
 * Repository interface for {@link AuditLogControl} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AuditLogControlRepository extends BaseRepository<AuditLogControl, String> {

	/**
	 * Fetch the AuditLogFromDateTime and AuditLogToDateTime of the latest
	 * {@link AuditLogControl}
	 * 
	 * @return the {@link RegistrationAuditDates}
	 */
	RegistrationAuditDates findTopByOrderByCrDtimeDesc();

	/**
	 * Fetch the Audit Logs upto req time
	 * 
	 * @param req
	 *            upto Audit logs to be retrieved
	 * @return list of audit log controls
	 */
	List<AuditLogControl> findByCrDtimeBefore(Timestamp req);

}
