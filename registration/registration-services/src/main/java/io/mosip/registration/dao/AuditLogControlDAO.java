package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationAuditDates;

/**
 * This class is used to control all the operations on the {@link AuditLogControl}.
 * This class is used to fetch/insert/delete the {@link AuditLogControl}.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public interface AuditLogControlDAO {

	/**
	 * This method is used to find the audit log start and end {@link Timestamp} of the latest 
	 * registration from {@link AuditLogControl}
	 * 
	 * @return {@link RegistrationAuditDates}
	 */
	RegistrationAuditDates getLatestRegistrationAuditDates();

	/**
	 * This method is used to save the {@link AuditLogControl} for the latest {@link Registration} packet
	 * 
	 * @param auditLogControl
	 *            the {@link AuditLogControl} object to be saved
	 */
	void save(AuditLogControl auditLogControl);

	/**
	 * This method is used to delete the audit log control
	 * 
	 * @param auditLogControl
	 *            the {@link AuditLogControl} object to be deleted
	 */
	void delete(AuditLogControl auditLogControl);

	/**
	 * This method is used to fetch the Audit Log Control
	 * 
	 * @param req
	 *            request time upto which logs need to be retrieved
	 *            
	 * @return list of Audit logs
	 */
	List<AuditLogControl> get(Timestamp req);
	
	/**
	 * This method is used to get Audit Log Control using registration Id
	 * 
	 * @param regId
	 *            the id of {@link Registration} entity for which
	 *            {@link AuditLogControl} to be fetched
	 *            
	 * @return returns the {@link AuditLogControl} object based on the input id of
	 *         {@link Registration}
	 */
	AuditLogControl get(String regId);

}
