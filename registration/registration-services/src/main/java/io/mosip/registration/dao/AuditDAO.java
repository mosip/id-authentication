package io.mosip.registration.dao;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.registration.entity.RegistrationAuditDates;

/**
 * This class is used to fetch/delete audit related information to {@link Audit} table.
 * DAO class for Audit
 * 
 * @author Balaji Sridharan
 * @author Yaswanth S
 * @since 1.0.0
 */
public interface AuditDAO {

	/**
	 * This method is used to delete all audit rows which are present in between the given specific time.
	 * 
	 * @param auditLogFromDtimes
	 *            startTime
	 *            
	 * @param auditLogToDtimes
	 *            end time
	 */
	void deleteAll(LocalDateTime auditLogFromDtimes, LocalDateTime auditLogToDtimes);

	/**
	 * This method is used to retrieve the {@link Audit} logs which are yet to be synchronized to the
	 * server along with the registration packet
	 * 
	 * @param registrationAuditDates
	 *            the start and end DateTimes of the audits synchronized with last
	 *            registration packet
	 *            
	 * @return the {@link Audit} logs to be synchronized to the server with
	 *         registration packet
	 */
	List<Audit> getAudits(RegistrationAuditDates registrationAuditDates);

}
