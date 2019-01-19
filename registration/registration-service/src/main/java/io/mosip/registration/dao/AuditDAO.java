package io.mosip.registration.dao;

import java.util.List;

import io.mosip.kernel.auditmanager.entity.Audit;

/**
 * DAO class for Audit
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AuditDAO {

	/**
	 * Retrieves list of {@link Audit} Logs which are yet to be synchronized with
	 * the server
	 * 
	 * @return the list of unsynchronized {@link Audit} logs
	 */
	List<Audit> getAllUnsyncAudits();

	/**
	 * Updates the {@link Audit} logs which are synchronized with the server
	 * 
	 * @param auditUUIDs
	 *            the list of Audit's UUIDs to be updated
	 * @return the number of records updated
	 */
	int updateSyncAudits(List<String> auditUUIDs);

}
