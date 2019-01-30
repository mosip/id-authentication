package io.mosip.registration.dao;

import java.sql.Timestamp;

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
	 */
	void save(AuditLogControl auditLogControl);

}
