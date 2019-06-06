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
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.ValidDocumentErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryAndDocTypeResponseDto;
import io.mosip.kernel.masterdata.dto.ValidDocCategoryDto;
import io.mosip.kernel.masterdata.dto.ValidDocumentDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.ValidDocumentExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocCategoryAndTypeResponseDto;
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
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This service class contains methods that create and delete valid document.
 * 
 * @author Ritesh Sinha
 * @author Neha Sinha
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

}
