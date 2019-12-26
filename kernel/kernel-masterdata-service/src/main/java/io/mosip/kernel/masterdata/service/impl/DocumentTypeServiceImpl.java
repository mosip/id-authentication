package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import io.mosip.kernel.masterdata.dto.getresponse.DocumentTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocumentTypePostResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocumentTypePutResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Device;
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
import io.mosip.kernel.masterdata.utils.MasterdataCreationUtil;
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
	FilterColumnValidator filterColumnValidator;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private FilterTypeValidator filterTypeValidator;

	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	@Autowired
	private PageUtils pageUtils;
	
	@Autowired
	private MasterdataCreationUtil masterdataCreationUtil;

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
			listOfDocumentTypeDto = new ArrayList<>();
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
	public DocumentTypePostResponseDto createDocumentType(DocumentTypeDto documentTypeDto) {
	
		
		DocumentType documentType = new DocumentType();
		DocumentTypePostResponseDto documentTypePostResponseDto = new DocumentTypePostResponseDto();
		try {
			documentTypeDto = masterdataCreationUtil.createMasterData(DocumentType.class, documentTypeDto);
			DocumentType entity = MetaDataUtils.setCreateMetaData(documentTypeDto, DocumentType.class);
			documentType = documentTypeRepository.create(entity);
			MapperUtils.map(documentType, documentTypePostResponseDto);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_REQUEST_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e1));
		}


		return documentTypePostResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#updateDocumentType(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public DocumentTypePutResponseDto updateDocumentType(DocumentTypeDto documentTypeDto) {
		try {
			DocumentType documentType = documentTypeRepository.findByCodeAndLangCode(documentTypeDto.getCode(),
					documentTypeDto.getLangCode());
			if (documentType != null) {

				if ((documentTypeDto.getIsActive() == Boolean.TRUE) && (documentType.getIsActive() == Boolean.TRUE)) {
					throw new RequestException(
							DocumentTypeErrorCode.DOCUMENT_TYPE_REACTIVATION_EXCEPTION.getErrorCode(),
							DocumentTypeErrorCode.DOCUMENT_TYPE_REACTIVATION_EXCEPTION.getErrorMessage());
				} else if ((documentTypeDto.getIsActive() == Boolean.FALSE)
						&& (documentType.getIsActive() == Boolean.FALSE)) {
					throw new RequestException(
							DocumentTypeErrorCode.DOCUMENT_TYPE_REDEACTIVATION_EXCEPTION.getErrorCode(),
							DocumentTypeErrorCode.DOCUMENT_TYPE_REDEACTIVATION_EXCEPTION.getErrorMessage());
				}
				documentTypeDto = masterdataCreationUtil.updateMasterData(DocumentType.class, documentTypeDto);
				MetaDataUtils.setUpdateMetaData(documentTypeDto, documentType,true);
				documentTypeRepository.update(documentType);
			} else {
				throw new RequestException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_UPDATE_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
		}
		DocumentTypePutResponseDto documentTypePutResponseDto = new DocumentTypePutResponseDto();

		MapperUtils.mapFieldValues(documentTypeDto, documentTypePutResponseDto);

		return documentTypePutResponseDto;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentTypeService#
	 * documentTypeFilterValues(io.mosip.kernel.masterdata.dto.request.
	 * FilterValueDto)
	 */
	@Override
	public FilterResponseDto documentTypeFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), DocumentType.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				masterDataFilterHelper.filterValues(DocumentType.class, filterDto, filterValueDto)
						.forEach(filterValue -> {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.DocumentTypeService#searchDocumentTypes(io
	 * .mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<DocumentTypeExtnDto> searchDocumentTypes(SearchDto dto) {
		PageResponseDto<DocumentTypeExtnDto> pageDto = new PageResponseDto<>();
		List<DocumentTypeExtnDto> doumentTypes = null;
		if (filterTypeValidator.validate(DocumentTypeExtnDto.class, dto.getFilters())) {
			pageUtils.validateSortField(DocumentType.class, dto.getSort());
			Page<DocumentType> page = masterdataSearchHelper.searchMasterdata(DocumentType.class, dto, null);
			if (page.getContent() != null && !page.getContent().isEmpty()) {
				pageDto = PageUtils.pageResponse(page);
				doumentTypes = MapperUtils.mapAll(page.getContent(), DocumentTypeExtnDto.class);
				pageDto.setData(doumentTypes);
			}
		}
		return pageDto;
	}

	@Override
	public DocumentTypeResponseDto getAllDocumentTypeByLaguageCode(String langCode) {
		DocumentTypeResponseDto documentTypeResponseDto = new DocumentTypeResponseDto();
		List<DocumentTypeDto> documentTypeDtoList = new ArrayList<>();
		List<DocumentType> documentTypesList = new ArrayList<>();
		try {
			documentTypesList = documentTypeRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(DocumentTypeErrorCode.DOCUMENT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					e.getMessage() + ExceptionUtils.parseException(e));
		}

		if (!(documentTypesList.isEmpty())) {
			documentTypesList.forEach(documentType -> {
				DocumentTypeDto documentTypeDto = new DocumentTypeDto();
				MapperUtils.map(documentType, documentTypeDto);
				documentTypeDtoList.add(documentTypeDto);
			});
		} else {
			throw new DataNotFoundException(DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentTypeErrorCode.DOCUMENT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		documentTypeResponseDto.setDocumenttypes(documentTypeDtoList);
		return documentTypeResponseDto;
	}

}