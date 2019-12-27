package io.mosip.preregistration.document.repository.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.entity.DocumentEntity;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.document.code.DocumentStatusMessages;
import io.mosip.preregistration.document.errorcodes.ErrorCodes;
import io.mosip.preregistration.document.errorcodes.ErrorMessages;
import io.mosip.preregistration.document.exception.DocumentNotFoundException;
import io.mosip.preregistration.document.repository.DocumentRepository;
import io.mosip.preregistration.document.service.util.DocumentServiceUtil;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class DocumentDAO {

	/** Autowired reference for {@link #documentRepository}. */
	@Autowired
	@Qualifier("documentRepository")
	private DocumentRepository documentRepository;

	@Autowired
	private DocumentServiceUtil serviceUtil;

	/**
	 * Logger configuration for DocumnetDAO
	 */
	private static Logger log = LoggerConfiguration.logConfig(DocumentDAO.class);

	public List<DocumentEntity> findBypreregId(String preId) {
		List<DocumentEntity> entityList = null;
		try {
			entityList = documentRepository.findByDemographicEntityPreRegistrationId(preId);
			if (serviceUtil.isNull(entityList)) {
				throw new DocumentNotFoundException(ErrorCodes.PRG_PAM_DOC_005.toString(),
						DocumentStatusMessages.DOCUMENT_IS_MISSING.getMessage());
			}
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In findBydocumentId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
		return entityList;
	}

	public DocumentEntity findBydocumentId(String documentId) {
		DocumentEntity entity = null;
		try {
			entity = documentRepository.findBydocumentId(documentId);
			if (entity == null) {
				throw new DocumentNotFoundException(ErrorCodes.PRG_PAM_DOC_005.toString(),
						DocumentStatusMessages.DOCUMENT_IS_MISSING.getMessage());
			}
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In findBydocumentId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
		return entity;
	}

	public DocumentEntity findSingleDocument(String preId, String catCode) {
		DocumentEntity entity = null;
		try {
			entity = documentRepository.findSingleDocument(preId, catCode);
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In findSingleDocument method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
		return entity;
	}

	public int deleteAllBydocumentId(String documentId) {

		try {
			return documentRepository.deleteAllBydocumentId(documentId);
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In deleteAllBydocumentId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
	}

	public int deleteAllBypreregId(String preregId) {
		try {
			return documentRepository.deleteAllByDemographicEntityPreRegistrationId(preregId);
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In deleteAllBypreregId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
	}

	public boolean existsByPreregId(String preregId) {
		return documentRepository.existsByDemographicEntityPreRegistrationId(preregId);
	}

	public DocumentEntity saveDocument(DocumentEntity entity) {
		try {
			return documentRepository.save(entity);
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In saveDocument method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.getMessage(), ex.getCause());
		}
	}

}
