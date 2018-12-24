package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.ValidDocument;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.repository.ValidDocumentRepository;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch list of valid document category, create
 * document category based on provided data,update document category based on
 * data provided and delete document category based on id provided.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class DocumentCategoryServiceImpl implements DocumentCategoryService {

	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	@Autowired
	private ValidDocumentRepository validDocumentRepository;

	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	private DocumentCategoryResponseDto documentCategoryResponseDto = new DocumentCategoryResponseDto();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * getAllDocumentCategory()
	 */
	@Override
	public DocumentCategoryResponseDto getAllDocumentCategory() {
		List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();
		try {
			documentCategoryList = documentCategoryRepository
					.findAllByIsDeletedFalseOrIsDeletedIsNull(DocumentCategory.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategory -> {
				DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
				MapperUtils.map(documentCategory, documentCategoryDto);
				documentCategoryDtoList.add(documentCategoryDto);
			});
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtoList);
		return documentCategoryResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * getAllDocumentCategoryByLaguageCode(java.lang.String)
	 */
	@Override
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(String langCode) {
		List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();
		try {
			documentCategoryList = documentCategoryRepository
					.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategory -> {
				DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
				MapperUtils.map(documentCategory, documentCategoryDto);
				documentCategoryDtoList.add(documentCategoryDto);
			});
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtoList);
		return documentCategoryResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * getDocumentCategoryByCodeAndLangCode(java.lang.String, java.lang.String)
	 */
	@Override
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(String code, String langCode) {
		List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();
		DocumentCategory documentCategory;
		DocumentCategoryDto documentCategoryDto;
		try {
			documentCategory = documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(code,
					langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (documentCategory != null) {
			documentCategoryDto = MapperUtils.map(documentCategory, DocumentCategoryDto.class);
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		documentCategoryDtoList.add(documentCategoryDto);
		documentCategoryResponseDto.setDocumentcategories(documentCategoryDtoList);
		return documentCategoryResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * createDocumentCategory(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createDocumentCategory(RequestDto<DocumentCategoryDto> category) {
		DocumentCategory entity = MetaDataUtils.setCreateMetaData(category.getRequest(), DocumentCategory.class);
		DocumentCategory documentCategory;
		try {
			documentCategory = documentCategoryRepository.create(entity);

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(documentCategory, codeLangCodeId);

		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * updateDocumentCategory(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID updateDocumentCategory(RequestDto<DocumentCategoryDto> category) {

		DocumentCategoryDto categoryDto = category.getRequest();

		CodeAndLanguageCodeID documentCategoryId = new CodeAndLanguageCodeID();

		MapperUtils.mapFieldValues(categoryDto, documentCategoryId);
		try {

			DocumentCategory documentCategory = documentCategoryRepository.findById(DocumentCategory.class,
					documentCategoryId);

			if (documentCategory != null) {
				MetaDataUtils.setUpdateMetaData(categoryDto, documentCategory, false);
				documentCategoryRepository.update(documentCategory);
			} else {
				throw new DataNotFoundException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_UPDATE_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_UPDATE_EXCEPTION.getErrorMessage());
		}
		return documentCategoryId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.DocumentCategoryService#
	 * deleteDocumentCategory(java.lang.String)
	 */
	@Override
	@Transactional
	public CodeResponseDto deleteDocumentCategory(String code) {

		try {
			List<ValidDocument> validDocument = validDocumentRepository.findByDocCategoryCode(code);

			if (!validDocument.isEmpty()) {
				throw new MasterDataServiceException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
			}

			int updatedRows = documentCategoryRepository.deleteDocumentCategory(LocalDateTime.now(ZoneId.of("UTC")),
					code);
			if (updatedRows < 1) {

				throw new DataNotFoundException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_DELETE_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_DELETE_EXCEPTION.getErrorMessage());
		}
		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(code);
		return responseDto;

	}

}
