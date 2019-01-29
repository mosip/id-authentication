package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.DocumentFormatDAO;
import io.mosip.registration.entity.DocumentFormat;
import io.mosip.registration.repositories.DocumentFormatRepository;

/**
 * implementation class of {@link DocumentFormatDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class DocumentFormatDAOImpl implements DocumentFormatDAO {
	/** instance of {@link DocumentFormatRepository} */
	@Autowired
	private DocumentFormatRepository documentFormatRepository;
	/** instance of {@link Logger} */

	private static final Logger LOGGER = AppConfig.getLogger(DocumentFormatDAOImpl.class);

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.DocumentFormatDAO#getDocumentFormats()
	 */

	@Override
	public List<DocumentFormat> getDocumentFormats() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTFORMATDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the documentformats");

		return documentFormatRepository.findAll();
	}

}
