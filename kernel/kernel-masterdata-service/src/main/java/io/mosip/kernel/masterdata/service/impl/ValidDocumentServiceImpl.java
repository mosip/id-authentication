package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.ValidDocumentErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryAndDocTypeResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentCategoryExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentCategoryTypeMappingExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.ValidDocumentExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocCategoryAndTypeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.DocumentType;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.ValidDocumentID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.DocumentTypeRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.service.ValidDocumentService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.OptionalFilter;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;

/**
 * This service class contains methods that create and delete valid document.
 * 
 * @author Ritesh Sinha
 * @author Neha Sinha
 * @author Sidhant Agarwal
 * 
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class ValidDocumentServiceImpl implements ValidDocumentService {

	/**
	 * Reference to ValidDocumentRepository.
	 */

	@Autowired
	private ValidDocumentRepository documentRepository;

	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	@Autowired
	private MasterdataSearchHelper masterdataSearchHelper;

	@Autowired
	FilterColumnValidator filterColumnValidator;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.ValidDocumentService#
	 * insertDocumentCategory(io.mosip.kernel.masterdata.dto.
	 * ValidDocumentRequestDto)
	 */
	@Override
	public ValidDocumentID createValidDocument(ValidDocumentDto document) {

		ValidDocument validDocument = MetaDataUtils.setCreateMetaData(document, ValidDocument.class);
		try {
			validDocument = documentRepository.create(validDocument);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}

		ValidDocumentID validDocumentId = new ValidDocumentID();
		validDocumentId.setDocCategoryCode(validDocument.getDocCategoryCode());
		validDocumentId.setDocTypeCode(validDocument.getDocTypeCode());
		return validDocumentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ValidDocumentService#deleteValidDocuemnt(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public DocCategoryAndTypeResponseDto deleteValidDocuemnt(String docCatCode, String docTypeCode) {
		try {
			int updatedRows = documentRepository.deleteValidDocument(LocalDateTime.now(ZoneId.of("UTC")), docCatCode,
					docTypeCode, MetaDataUtils.getContextUser());

			if (updatedRows < 1) {

				throw new RequestException(ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorCode(),
						ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ValidDocumentErrorCode.VALID_DOCUMENT_DELETE_EXCEPTION.getErrorCode(),
					ValidDocumentErrorCode.VALID_DOCUMENT_DELETE_EXCEPTION.getErrorMessage());
		}
		DocCategoryAndTypeResponseDto responseDto = new DocCategoryAndTypeResponseDto();
		responseDto.setDocCategoryCode(docCatCode);
		responseDto.setDocTypeCode(docTypeCode);
		return responseDto;
	}

	@Override
	public ValidDocCategoryAndDocTypeResponseDto getValidDocumentByLangCode(String langCode) {

		ValidDocCategoryAndDocTypeResponseDto validDocCategoryAndDocTypeResponseDto = new ValidDocCategoryAndDocTypeResponseDto();
		List<ValidDocCategoryDto> categoryDtos = new ArrayList<>();

		try {
			List<DocumentCategory> documentCategories = documentCategoryRepository
					.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
			for (DocumentCategory documentCategory : documentCategories) {
				List<DocumentType> documentTypes = documentTypeRepository
						.findByCodeAndLangCodeAndIsDeletedFalse(documentCategory.getCode(), langCode);
				List<DocumentTypeDto> documentTypeDtos = MapperUtils.mapAll(documentTypes, DocumentTypeDto.class);
				ValidDocCategoryDto validDocCategoryDto = MapperUtils.map(documentCategory, ValidDocCategoryDto.class);
				validDocCategoryDto.setDocumenttypes(documentTypeDtos);
				categoryDtos.add(validDocCategoryDto);
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ValidDocumentErrorCode.VALID_DOCUMENT_FETCH_EXCEPTION.getErrorCode(),
					ValidDocumentErrorCode.VALID_DOCUMENT_FETCH_EXCEPTION.getErrorMessage());
		}
		if (EmptyCheckUtils.isNullEmpty(categoryDtos)
				|| EmptyCheckUtils.isNullEmpty(validDocCategoryAndDocTypeResponseDto)) {
			throw new MasterDataServiceException(
					ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorCode(),
					ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		validDocCategoryAndDocTypeResponseDto.setDocumentcategories(categoryDtos);
		return validDocCategoryAndDocTypeResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ValidDocumentService#getValidDocuments(
	 * int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<ValidDocumentExtnDto> getValidDocuments(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<ValidDocumentExtnDto> validDocs = null;
		PageDto<ValidDocumentExtnDto> pageDto = null;
		try {
			Page<ValidDocument> pageData = documentRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				validDocs = MapperUtils.mapAll(pageData.getContent(), ValidDocumentExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						validDocs);
			} else {
				throw new DataNotFoundException(
						ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorCode(),
						ValidDocumentErrorCode.VALID_DOCUMENT_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ValidDocumentErrorCode.VALID_DOCUMENT_FETCH_EXCEPTION.getErrorCode(),
					ValidDocumentErrorCode.VALID_DOCUMENT_FETCH_EXCEPTION.getErrorMessage());
		}
		return pageDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ValidDocumentService#searchValidDocument(
	 * io.mosip.kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageDto<DocumentCategoryTypeMappingExtnDto> searchValidDocument(SearchDto dto) {
		PageDto<DocumentCategoryTypeMappingExtnDto> pageDto = new PageDto<>();
		List<DocumentCategoryTypeMappingExtnDto> validDocs = new ArrayList<>();
		List<SearchFilter> addList = new ArrayList<>();
		List<SearchFilter> removeList = new ArrayList<>();
		List<SearchFilter> addList1 = new ArrayList<>();
		for (SearchFilter filter : dto.getFilters()) {
			String column = filter.getColumnName();
			if (column.equalsIgnoreCase("docCategoryCode")) {

				Page<ValidDocument> documents = masterdataSearchHelper.searchMasterdata(ValidDocument.class,
						new SearchDto(Arrays.asList(filter), Collections.emptyList(), new Pagination(), null), null);
				if (!documents.hasContent()) {
					throw new RequestException(ValidDocumentErrorCode.DOCUMENT_CATEGORY_NOT_FOUND.getErrorCode(),
							ValidDocumentErrorCode.DOCUMENT_CATEGORY_NOT_FOUND.getErrorMessage());
				}
				removeList.add(filter);
				addList.addAll(buildValidDocumentTypeSearchFilter(documents.getContent()));
				addList1.addAll(buildValidDocumentCategorySearchFilter(documents.getContent()));
			}
		}
		dto.getFilters().removeAll(removeList);
		Page<DocumentType> page = masterdataSearchHelper.searchMasterdata(DocumentType.class, dto,
				new OptionalFilter[] { new OptionalFilter(addList) });
		Page<DocumentCategory> pageCategory = masterdataSearchHelper.searchMasterdata(DocumentCategory.class, dto,
				new OptionalFilter[] { new OptionalFilter(addList1) });
		if ((page.getContent() != null && !page.getContent().isEmpty())
				&& (pageCategory.getContent() != null && !pageCategory.getContent().isEmpty())) {

			page.getContent().forEach(documentType -> {
				DocumentCategoryTypeMappingExtnDto documentTypeExtnDto = new DocumentCategoryTypeMappingExtnDto();
				pageCategory.getContent().forEach(documentCategory -> {

					new DocumentCategoryExtnDto();
					documentTypeExtnDto.setCode(documentCategory.getCode());

					documentTypeExtnDto.setDescription(documentCategory.getDescription());
					documentTypeExtnDto.setIsActive(documentCategory.getIsActive());

					documentTypeExtnDto.setLangCode(documentCategory.getLangCode());
					documentTypeExtnDto.setName(documentCategory.getName());

				});

				DocumentTypeExtnDto documentDto = new DocumentTypeExtnDto();

				documentDto.setCreatedBy(documentType.getCreatedBy());
				documentDto.setCreatedDateTime(documentType.getCreatedDateTime());
				documentDto.setDeletedDateTime(documentType.getDeletedDateTime());
				documentDto.setIsDeleted(documentType.getIsDeleted());
				documentDto.setDescription(documentType.getDescription());
				documentDto.setCode(documentType.getCode());
				documentDto.setIsActive(documentType.getIsActive());
				documentDto.setUpdatedBy(documentType.getUpdatedBy());
				documentDto.setUpdatedDateTime(documentType.getUpdatedDateTime());
				documentDto.setLangCode(documentType.getLangCode());
				documentDto.setName(documentType.getName());
				documentTypeExtnDto.setDocumentType(documentDto);
				validDocs.add(documentTypeExtnDto);
			});

			pageDto.setData(validDocs);
			pageDto.setPageNo(page.getNumber());
			pageDto.setTotalItems(page.getTotalElements());
			pageDto.setTotalPages(page.getTotalPages());
		}
		return pageDto;

	}

	/**
	 * Method to map to document type table to search doc cat-type mapping
	 * 
	 * @param validDoc
	 *            list of valid document values
	 * @return list of search filter
	 */
	private List<SearchFilter> buildValidDocumentTypeSearchFilter(List<ValidDocument> validDoc) {
		if (validDoc != null && !validDoc.isEmpty())
			return validDoc.stream().filter(Objects::nonNull).map(this::buildFilterDocumentType)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * method to return list of document type code
	 * 
	 * @param doc
	 *            value of valid document table
	 * @return list of document type code in search filter
	 */
	public SearchFilter buildFilterDocumentType(ValidDocument doc) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("code");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(doc.getDocTypeCode());
		return filter;
	}

	/**
	 * Method to map to document category table to search doc cat-type mapping
	 * 
	 * @param validDoc
	 *            list of valid document values
	 * @return list of search filter
	 */
	private List<SearchFilter> buildValidDocumentCategorySearchFilter(List<ValidDocument> validDoc) {
		if (validDoc != null && !validDoc.isEmpty())
			return validDoc.stream().filter(Objects::nonNull).map(this::buildFilterDocumentCategory)
					.collect(Collectors.toList());
		return Collections.emptyList();
	}

	/**
	 * method to return list of doc-category code
	 * 
	 * @param value
	 *            of valid document table
	 * @return list of document category code in search filter
	 */
	public SearchFilter buildFilterDocumentCategory(ValidDocument doc) {
		SearchFilter filter = new SearchFilter();
		filter.setColumnName("code");
		filter.setType(FilterTypeEnum.EQUALS.name());
		filter.setValue(doc.getDocCategoryCode());
		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.ValidDocumentService#
	 * categoryTypeFilterValues(io.mosip.kernel.masterdata.dto.request.
	 * FilterValueDto)
	 */
	@Override
	public FilterResponseDto categoryTypeFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		if (filterColumnValidator.validate(FilterDto.class, filterValueDto.getFilters(), ValidDocument.class)) {
			for (FilterDto filterDto : filterValueDto.getFilters()) {
				List<?> filterValues = masterDataFilterHelper.filterValues(ValidDocument.class, filterDto,
						filterValueDto);

				filterValues.forEach(filterValue -> {
					ColumnValue columnValue = new ColumnValue();
					columnValue.setFieldID(filterDto.getColumnName());
					columnValue.setFieldValue(filterValue.toString());
					columnValueList.add(columnValue);
				});
			}
			filterResponseDto.setFilters(columnValueList);
		}
		return filterResponseDto;
	}

}
