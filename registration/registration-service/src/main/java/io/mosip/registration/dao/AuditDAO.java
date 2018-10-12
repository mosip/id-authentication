/**
 * 
 */
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
	 * Retrieves all the Audit Logs
	 * 
	 * @return the Audit list
	 */
	List<Audit> getAllAudits();
}
