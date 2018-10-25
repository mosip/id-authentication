package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegAuditRepository;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.LOG_AUDIT_DAO;

/**
 * The implementation class of {@link AuditDAO}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
public class AuditDAOImpl implements AuditDAO {

	@Autowired
	private RegAuditRepository regAuditRepository;

	/** Object for Logger. */
	private static MosipLogger logger;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.AuditDAO#getAllAudits()
	 */
	@Override
	public List<Audit> getAllUnsyncAudits() {
		logger.debug(LOG_AUDIT_DAO, APPLICATION_NAME,
				APPLICATION_ID, "Fetching the list of unsync'ed Audits");
		try {
			return regAuditRepository.findAllUnsyncAudits();
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.FETCH_UNSYNC_AUDIT,
					runtimeException.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.AuditDAO#updateSyncAudits()
	 */
	@Override
	@Transactional
	public int updateSyncAudits(List<String> auditUUIDs) {
		logger.debug(LOG_AUDIT_DAO, APPLICATION_NAME,
				APPLICATION_ID, "updateSyncAudits has been started");
		int updatedCount = 0;
		try {
			int syncAuditCount = auditUUIDs.size();
			int endIndex = 0;
			int updateLimit = 50;

			for (int updatedAuditCount = 0; updatedAuditCount < syncAuditCount; updatedAuditCount += updateLimit) {
				endIndex += updateLimit;
				if ((syncAuditCount - endIndex) < 0) {
					endIndex = syncAuditCount;
				}
				updatedCount += regAuditRepository.updateSyncAudits(auditUUIDs.subList(updatedAuditCount, endIndex));
			}
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.UPDATE_SYNC_AUDIT,
					runtimeException.toString());
		}
		logger.debug(LOG_AUDIT_DAO, APPLICATION_NAME,
				APPLICATION_ID, "updateSyncAudits has been ended");
		return updatedCount;
	}

}
