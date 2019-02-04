package io.mosip.registration.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.AuditDAO;
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

	@Autowired
	private AuditDAO auditDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.AuditLogControlDAO#getLatestRegistrationAuditDates(
	 * )
	 */
	@Override
	public RegistrationAuditDates getLatestRegistrationAuditDates() {
		LOGGER.info("AUDIT - GET_LATEST_REGISTRATION_AUDIT_DATES - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME,
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
		LOGGER.info("AUDIT - SAVE_AUDIT_LOG_CONTROL - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME,
				APPLICATION_ID, "Saves the audit log control for the latest registration packet");

		auditLogControlRepository.save(auditLogControl);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.AuditLogControlDAO#delete(io.mosip.registration.entity.AuditLogControl)
	 */
	@Override
	public void delete(AuditLogControl auditLogControl) {

		LOGGER.debug("AUDIT - DELETE_AUDIT_LOG_CONTROL - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Started Deleting the audit log control for the registration packet");

		/* Delete Audit Logs */
		auditDAO.deleteAll(auditLogControl.getAuditLogFromDateTime().toLocalDateTime(),
				auditLogControl.getAuditLogToDateTime().toLocalDateTime());

		/* Delete Audit Control Log */
		auditLogControlRepository.delete(auditLogControl);

		LOGGER.debug("AUDIT - DELETE_AUDIT_LOG_CONTROL - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Started Deleting the audit log control for the registration packet");

	}

	@Override
	public List<AuditLogControl> get(Timestamp req) {
		LOGGER.debug("AUDIT - GET_AUDIT_LOG_CONTROL - AUDIT_LOG_CONTROL_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Started fetching List of audit log control  before req Time");


		return auditLogControlRepository.findByCrDtimeBefore(req);
	}

}
