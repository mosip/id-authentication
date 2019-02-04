package io.mosip.registration.dao.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.entity.RegistrationAuditDates;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegAuditRepository;

import static io.mosip.registration.constants.LoggerConstants.LOG_AUDIT_DAO;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * The implementation class of {@link AuditDAO}
 * 
 * @author Balaji Sridharan
 * @author Yaswanth S
 * @since 1.0.0
 */
@Repository
public class AuditDAOImpl implements AuditDAO {

	@Autowired
	private RegAuditRepository regAuditRepository;

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(AuditDAOImpl.class);

	@Override
	@Transactional
	public void deleteAll(LocalDateTime auditLogFromDtimes, LocalDateTime auditLogToDtimes) {
		LOGGER.info(LOG_AUDIT_DAO, APPLICATION_NAME,
				APPLICATION_ID, "Deleting Audit Logs");
		regAuditRepository.deleteAllInBatchBycreatedAtBetween(auditLogFromDtimes, auditLogToDtimes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.AuditDAO#getAudits(io.mosip.registration.entity.
	 * RegistrationAuditDates)
	 */
	@Override
	public List<Audit> getAudits(RegistrationAuditDates registrationAuditDates) {
		LOGGER.info("REGISTRATION - FETCH_UNSYNCED_AUDITS - GET_ALL_AUDITS", APPLICATION_NAME, APPLICATION_ID,
				"Fetching of unsynchronized which are to be added to Registartion packet started");

		try {
			List<Audit> audits;
			if (registrationAuditDates == null || registrationAuditDates.getAuditLogToDateTime() == null) {
				audits = regAuditRepository.findAllByOrderByCreatedAtAsc();
			} else {
				audits = regAuditRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(
						registrationAuditDates.getAuditLogToDateTime().toLocalDateTime());
			}

			LOGGER.info("REGISTRATION - FETCH_UNSYNCED_AUDITS - GET_ALL_AUDITS", APPLICATION_NAME, APPLICATION_ID,
					"Fetching of unsynchronized which are to be added to Registartion packet ended");

			return audits;
		} catch (RuntimeException exception) {
			throw new RegBaseUncheckedException(RegistrationConstants.UPDATE_SYNC_AUDIT, exception.getMessage(),
					exception);
		}
	}

}
