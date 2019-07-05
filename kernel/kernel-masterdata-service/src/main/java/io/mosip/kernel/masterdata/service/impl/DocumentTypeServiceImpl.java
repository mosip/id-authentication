package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.DocumentTypeErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

/**
 * This class have methods to fetch list of valid document type, create document
 * type based on provided data,update document type based on data provided and
 * delete document type based on id provided.
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class DocumentTypeServiceImpl implements DocumentTypeService {

	/**
	 * Reference to DocumentTypeRepository.
	 */
	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	/**
	 * Reference to ValidDocumentRepository.
	 */
	@Autowired
	private ValidDocumentRepository validDocumentRepository;
	
	@Autowired
	private FilterTypeValidator filterTypeValidator;
	
	private MasterdataSearchHelper masterdataSearchHelper;
	
	@Autowired
	FilterColumnValidator filterColumnValidator;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentTypeService#
	 * getAllValidDocumentType(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DocumentTypeDto> getAllValidDocumentType(String code, String langCode) {
		List<DocumentTypeDto> listOfDocumentTypeDto = null;
		List<DocumentType> documents = null;
		try {
			documents = documentTypeRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		if (documents != null && !documents.isEmpty()) {
			listOfDocumentTypeDto = MapperUtils.mapAll(documents, DocumentTypeDto.class);
		} else {
			throw new DataNotFoundException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
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
	public CodeAndLanguageCodeID createDocumentType(DocumentTypeDto documentTypeDto) {
		DocumentType entity = MetaDataUtils.setCreateMetaData(documentTypeDto, DocumentType.class);
		DocumentType documentType;
		try {
			documentType = documentTypeRepository.create(entity);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(documentType, codeLangCodeId);

		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#updateDocumentType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID updateDocumentType(DocumentTypeDto documentTypeDto) {
		try {
			DocumentType documentType = documentTypeRepository.findByCodeAndLangCode(documentTypeDto.getCode(),
					documentTypeDto.getLangCode());
			if (documentType != null) {
				MetaDataUtils.setUpdateMetaData(documentTypeDto, documentType, false);
			} else {
				throw new RequestException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
			documentTypeRepository.update(documentType);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_UPDATE_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID documentTypeId = new CodeAndLanguageCodeID();

		MapperUtils.mapFieldValues(documentTypeDto, documentTypeId);

		return documentTypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#deleteDocumentType(
	 * java.lang.String)
	 */
	@Override
	public CodeResponseDto deleteDocumentType(String code) {

		try {
			List<ValidDocument> validDocument = validDocumentRepository.findByDocTypeCode(code);
			if (!validDocument.isEmpty()) {
				throw new MasterDataServiceException(
						DocumentTypeErrorCode.DOCUMENT_TYPE_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
						DocumentTypeErrorCode.DOCUMENT_TYPE_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
			}

			int updatedRows = documentTypeRepository.deleteDocumentType(LocalDateTime.now(ZoneId.of("UTC")), code,
					MetaDataUtils.getContextUser());

			if (updatedRows < 1) {

				throw new RequestException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_DELETE_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_DELETE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(code);
		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#getAllDocumentTypes(
	 * int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<DocumentTypeExtnDto> getAllDocumentTypes(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<DocumentTypeExtnDto> documentTypes = null;
		PageDto<DocumentTypeExtnDto> pageDto = null;
		try {
			Page<DocumentType> pageData = documentTypeRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				documentTypes = MapperUtils.mapAll(pageData.getContent(), DocumentTypeExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						documentTypes);
			} else {
				throw new DataNotFoundException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(e));
		}
		return pageDto;
	}
	
	@Override
	public FilterResponseDto documentTypeFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters())) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				masterDataFilterHelper.filterValues(DocumentType.class, filterDto.getColumnName(),
						filterDto.getType(), filterValueDto.getLanguageCode()).forEach(filterValue -> {
							if (filterValue != null) {
								ColumnValue columnValue = new ColumnValue();
								columnValue.setFieldID(filterDto.getColumnName());
								columnValue.setFieldValue(filterValue.toString());
								columnValueList.add(columnValue);
							}
						});
			}
			filterResponseDto.setFilters(columnValueList);
		}
		return filterResponseDto;
	}

}