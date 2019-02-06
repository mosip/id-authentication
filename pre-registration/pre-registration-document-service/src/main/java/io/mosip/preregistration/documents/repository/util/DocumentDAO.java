package io.mosip.preregistration.documents.repository.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.documents.code.DocumentStatusMessages;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.errorcodes.ErrorCodes;
import io.mosip.preregistration.documents.errorcodes.ErrorMessages;
import io.mosip.preregistration.documents.exception.DocumentFailedToCopyException;
import io.mosip.preregistration.documents.exception.DocumentFailedToDeleteException;
import io.mosip.preregistration.documents.exception.DocumentNotFoundException;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.util.DocumentServiceUtil;


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
	
	
	public List<DocumentEntity> findBypreregId(String preId){
		 List<DocumentEntity> entityList= new ArrayList<>();
		 try {
			 entityList= documentRepository.findBypreregId(preId);
			/* if(serviceUtil.isNull(entityList)) {
				 throw new DocumentNotFoundException(DocumentStatusMessages.DOCUMENT_IS_MISSING.toString());
			 }*/
		 }catch (DataAccessLayerException ex) {
			 log.error("sessionId", "idType", "id", "In findBydocumentId method of DocumnetDAO - " + ex);
			 throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
						ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		 }
		 return entityList;
	}
	
	public DocumentEntity findBydocumentId(String documentId) {
		DocumentEntity entity= new DocumentEntity();
		try {
			entity=documentRepository.findBydocumentId(documentId);
			if(entity==null) {
				throw new DocumentNotFoundException(DocumentStatusMessages.DOCUMENT_IS_MISSING.toString());
			}
		}catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In findBydocumentId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		}
		return entity;
	}

	public DocumentEntity findSingleDocument(String preId, String catCode) {
		DocumentEntity entity= new DocumentEntity();
		try {
			entity=documentRepository.findSingleDocument(preId,catCode);
		}catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In findSingleDocument method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		}
		return entity;
	}
	
	public int deleteAllBydocumentId(String documentId) {

		try {
			return documentRepository.deleteAllBydocumentId(documentId);
		}catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In deleteAllBydocumentId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		}
	}
	
	public int deleteAllBypreregId(String preregId) {
		try {
			return documentRepository.deleteAllBypreregId(preregId);
		}catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In deleteAllBypreregId method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		}
	}
	
	public boolean existsByPreregId(String preregId) {
		return documentRepository.existsByPreregId(preregId);
	}

	public DocumentEntity saveDocument(DocumentEntity entity) {
		try {
			return documentRepository.save(entity);
		}catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id", "In saveDocument method of DocumnetDAO - " + ex);
			throw new TableNotAccessibleException(ErrorCodes.PRG_PAM_DOC_012.toString(),
					ErrorMessages.DOCUMENT_TABLE_NOTACCESSIBLE.toString(), ex.getCause());
		}
	}

}
