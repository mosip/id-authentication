package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
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
	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(DocumentTypeDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.DocumentTypeDAO#getDocumentTypes()
	 */
	@Override
	public List<DocumentType> getDocumentTypes() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTTYPESDAO", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document types");
		return documentTypeRepository.findAll();
	}

	@Override
	public List<DocumentType> getDocTypeByName(String docTypeName) {
		return documentTypeRepository.findByIsActiveTrueAndName(docTypeName);
	}

}
