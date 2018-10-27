package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import io.mosip.registration.dao.DocumentTypeDAO;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.repositories.DocumentTypeRepository;

/**
 * implementation class of {@link DocumentTypeDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class DocumentTypeDAOImpl implements DocumentTypeDAO {
	/** instance of {@link DocumentTypeRepository} */
	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	/** instance of {@link MosipLogger} */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger
	 * 
	 * @param mosipRollingFileAppender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.DocumentTypeDAO#getDocumentTypes()
	 */
	@Override
	public List<DocumentType> getDocumentTypes() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-DOCUMENTTYPESDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the document types");
		return documentTypeRepository.findAll();
	}

}
