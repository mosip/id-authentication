package io.mosip.registration.repositories;

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
	 * Fetch the AuditLogFromDateTime and AuditLogToDateTime of the latest {@link AuditLogControl}
	 * 
	 * @return the {@link RegistrationAuditDates}
	 */
	RegistrationAuditDates findTopByOrderByCrDtimeDesc();

}
