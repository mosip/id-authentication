package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.ValidDocumentDAO;
import io.mosip.registration.entity.ApplicantValidDocument;
import io.mosip.registration.repositories.ApplicantValidDocumentRepository;

/**
 * implementation class of RegistrationValidDocumentDAO.
 *
 * @author Brahmanada Reddy
 * @since 1.0.0
 */
@Repository
public class ValidDocumentDAOImpl implements ValidDocumentDAO {
	
	/**  instance of {@link ApplicantValidDocumentRepository}. */
	@Autowired
	private ApplicantValidDocumentRepository applicantValidDocumentRepository;
	
	/**  instance of {@link Logger}. */
	private static final Logger LOGGER = AppConfig.getLogger(ValidDocumentDAOImpl.class);

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.ValidDocumentDAO#getValidDocuments(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ApplicantValidDocument> getValidDocuments(String applicantType, String docCategoryCode) {
		LOGGER.info("GET_VALID_DOCUMENTS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Fetching Document details");
		return applicantValidDocumentRepository.findByValidDocumentAppTypeCodeAndDocumentCategoryCode(applicantType,
				docCategoryCode);

	}

}
