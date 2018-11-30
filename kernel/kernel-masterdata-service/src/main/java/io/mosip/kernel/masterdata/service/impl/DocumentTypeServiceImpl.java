package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentTypeData;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch list of valid document types and to create
 * document types based on list provided.
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	DataMapper dataMapper;

	@Autowired
	private DocumentTypeRepository documentTypeRepository;
	@Autowired
	private MapperUtils mapperUtil;

	@Override
	public List<DocumentTypeData> getAllValidDocumentType(String code, String langCode) {
		List<DocumentTypeData> listOfDocumentTypeDto = null;
		List<DocumentType> documents = null;
		try {
			documents = documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorMessage());
		}
		if (documents != null && !documents.isEmpty()) {
			listOfDocumentTypeDto = mapperUtil.mapAll(documents, DocumentTypeData.class);
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return listOfDocumentTypeDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#addDocumentTypes(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createDocumentTypes(RequestDto<DocumentTypeDto> documentTypeDto) {
		DocumentType entity = metaUtils.setCreateMetaData(documentTypeDto.getRequest().getDocumentType(),
				DocumentType.class);
		DocumentType documentType;
		try {
			documentType = documentTypeRepository.create(entity);

		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getErrorText());
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(documentType, codeLangCodeId, true, null, null, true);

		return codeLangCodeId;
	}
}
