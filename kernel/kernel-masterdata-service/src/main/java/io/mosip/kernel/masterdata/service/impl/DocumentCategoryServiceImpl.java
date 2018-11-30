package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryData;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * This class have methods to fetch list of valid document types and to create
 * document types based on list provided.
 * 
 * @author Neha
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class DocumentCategoryServiceImpl implements DocumentCategoryService {

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	private List<DocumentCategory> documentCategoryList = new ArrayList<>();

	private DocumentCategoryResponseDto documentCategoryResponseDto = new DocumentCategoryResponseDto();

	/**
	 * Method to fetch all Document category details
	 * 
	 * @return DocumentCategoryDTO list
	 * 
	 * @throws DocumentCategoryFetchException
	 *             If fails to fetch required Document category
	 * 
	 * @throws DocumentCategoryMappingException
	 *             If not able to map Document category entity with Document
	 *             category Dto
	 * 
	 * @throws DocumentCategoryNotFoundException
	 *             If given required Document category not found
	 */
	@Override
	public DocumentCategoryResponseDto getAllDocumentCategory() {
		List<DocumentCategoryData> documentCategoryDtoList = new ArrayList<>();
		try {
			documentCategoryList = documentCategoryRepository.findAllByIsDeletedFalse(DocumentCategory.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategory -> {
				DocumentCategoryData documentCategoryDto = new DocumentCategoryData();
				dataMapper.map(documentCategory, documentCategoryDto, true, null, null, true);
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

	/**
	 * Method to fetch all Document category details based on language code
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return DocumentCategoryDTO list
	 * 
	 * @throws DocumentCategoryFetchException
	 *             If fails to fetch required Document category
	 * 
	 * @throws DocumentCategoryMappingException
	 *             If not able to map Document category entity with Document
	 *             category Dto
	 * 
	 * @throws DocumentCategoryNotFoundException
	 *             If given required Document category not found
	 */
	@Override
	public DocumentCategoryResponseDto getAllDocumentCategoryByLaguageCode(String langCode) {
		List<DocumentCategoryData> documentCategoryDtoList = new ArrayList<>();
		try {
			documentCategoryList = documentCategoryRepository.findAllByLangCodeAndIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategory -> {
				DocumentCategoryData documentCategoryDto = new DocumentCategoryData();
				dataMapper.map(documentCategory, documentCategoryDto, true, null, null, true);
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

	/**
	 * Method to fetch A Document category details based on id and language code
	 * 
	 * @param code
	 *            The Id of Document Category
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return DocumentCategoryDTO
	 * 
	 * @throws DocumentCategoryFetchException
	 *             If fails to fetch required Document category
	 * 
	 * @throws DocumentCategoryMappingException
	 *             If not able to map Document category entity with Document
	 *             category Dto
	 * 
	 * @throws DocumentCategoryNotFoundException
	 *             If given required Document category not found
	 */
	@Override
	public DocumentCategoryResponseDto getDocumentCategoryByCodeAndLangCode(String code, String langCode) {
		List<DocumentCategoryData> documentCategoryDtoList = new ArrayList<>();
		DocumentCategory documentCategory;
		DocumentCategoryData documentCategoryDto;
		try {
			documentCategory = documentCategoryRepository.findByCodeAndLangCodeAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (documentCategory != null) {
			documentCategoryDto = dataMapper.map(documentCategory, DocumentCategoryData.class, true, null, null, true);
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
	 * addDocumentCategoriesData(io.mosip.kernel.masterdata.dto.
	 * DocumentCategoryRequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createDocumentCategory(RequestDto<DocumentCategoryDto> category) {
		DocumentCategory entity = metaUtils.setCreateMetaData(category.getRequest().getDocumentCategory(),
				DocumentCategory.class);
		DocumentCategory documentCategory;
		try {
			documentCategory = documentCategoryRepository.create(entity);

		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getErrorText());
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(documentCategory, codeLangCodeId, true, null, null, true);

		return codeLangCodeId;
	}

}
