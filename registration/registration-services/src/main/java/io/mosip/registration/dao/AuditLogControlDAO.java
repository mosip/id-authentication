package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationAuditDates;

/**
 * DAO interface for the {@link AuditLogControl} entity
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AuditLogControlDAO {

	/**
	 * Find the audit log start and end {@link Timestamp} of the latest registration
	 * from {@link AuditLogControl}
	 * 
	 * @return {@link RegistrationAuditDates}
	 */
	RegistrationAuditDates getLatestRegistrationAuditDates();

	/**
	 * Saves the {@link AuditLogControl} for the latest {@link Registration} packet
	 * 
	 * @param auditLogControl
	 *            the {@link AuditLogControl} object to be saved
	 */
	void save(AuditLogControl auditLogControl);

	/**
	 * Deletes the audit log control
	 * 
	 * @param auditLogControl
	 *            the {@link AuditLogControl} object to be deleted
	 */
	void delete(AuditLogControl auditLogControl);

	/**
	 * Get Audit Log Control
	 * 
	 * @param req
	 *            request time upto logs need to be retrieved
	 * @return list of Audit logs
	 */
	List<AuditLogControl> get(Timestamp req);
	
	/**
	 * Get Audit Log Control using registration Id
	 * 
	 * @param regId
	 *            the id of {@link Registration} entity for which
	 *            {@link AuditLogControl} to be fetched
	 * @return returns the {@link AuditLogControl} object based on the input id of
	 *         {@link Registration}
	 */
	AuditLogControl get(String regId);

}
