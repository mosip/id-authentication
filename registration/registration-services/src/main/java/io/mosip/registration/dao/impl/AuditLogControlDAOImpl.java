package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.repositories.AuditLogControlRepository;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * DAO class for the {@link AuditLogControl} entity
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
public class AuditLogControlDAOImpl implements AuditLogControlDAO {

	private static final Logger LOGGER = AppConfig.getLogger(RegistrationDAOImpl.class);
	@Autowired
	private AuditLogControlRepository auditLogControlRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.AuditLogControlDAO#getLatestRegistrationAuditDates(
	 * )
	 */
	@Override
	public RegistrationAuditDates getLatestRegistrationAuditDates() {
		LOGGER.debug("AUDIT - GET_LATEST_REGISTRATION_AUDIT_DATES - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME,
				APPLICATION_ID, "Retrieving the latest audit logs start and end timestamps stored");

		return auditLogControlRepository.findTopByOrderByCrDtimeDesc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.AuditLogControlDAO#save(io.mosip.registration.
	 * entity.AuditLogControl)
	 */
	@Override
	public void save(AuditLogControl auditLogControl) {
		LOGGER.debug("AUDIT - SAVE_AUDIT_LOG_CONTROL - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME,
				APPLICATION_ID, "Saves the audit log control for the latest registration packet");

		auditLogControlRepository.save(auditLogControl);
	}

}
