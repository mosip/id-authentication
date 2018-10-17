package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.DocumentCategoryDAO;
import io.mosip.registration.entity.DocumentCategory;
import io.mosip.registration.repositories.DocumentCategoryRepository;
import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * implementation class of RegistrationDocumentCategoryDAOImpl
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class DocumentCategoryDAOImpl implements DocumentCategoryDAO {
	/** instance of {@link DocumentCategoryRepository} */
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;
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

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao#getDocumentCategories
	 */
	@Override
	public List<DocumentCategory> getDocumentCategories() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "fetching the document categories");

		return documentCategoryRepository.findAll();
	}

}
