package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.dao.ValidDocumentDAO;
import io.mosip.registration.entity.ValidDocument;
import io.mosip.registration.repositories.ValidDocumentRepository;

/**
 * implementation class of RegistrationValidDocumentDAO
 * 
 * @author Brahmanada Reddy
 * @since 1.0.0
 *
 */
@Repository
public class ValidDocumentDAOImpl implements ValidDocumentDAO {
	/** instance of {@link ValidDocumentRepository} */
	@Autowired
	private ValidDocumentRepository validDocumentRepository;
	/** instance of {@link MosipLogger} */
	private static MosipLogger LOGGER;

	/**
	 * Initialize the logger
	 * 
	 * @param mosipRollingFileAppender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.ValidDocumentDAO#getValidDocuments()
	 */
	@Override
	public List<ValidDocument> getValidDocuments() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-VALIDDOCUMENTDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the validdocuments");

		return validDocumentRepository.findAll();
	}

}
