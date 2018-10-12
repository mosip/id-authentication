/**
 * 
 */
package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.auditmanager.repository.AuditRepository;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.AuditDAO;

/**
 * The implementation class of {@link AuditDAO}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Repository
public class AuditDAOImpl implements AuditDAO {

	@Autowired
	private AuditRepository auditRepository;

	/** Object for Logger. */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender
	 *            the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.AuditDAO#getAllAudits()
	 */
	@Override
	public List<Audit> getAllAudits() {
		LOGGER.debug("REGISTRATION - AUDITS - AUDIT_DAO", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Audits had been ended");
		return auditRepository.findAll();
	}

}
