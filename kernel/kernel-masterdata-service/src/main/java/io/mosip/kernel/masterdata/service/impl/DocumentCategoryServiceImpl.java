package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryRequestDto;
import io.mosip.kernel.masterdata.dto.DocumentCategoryResponseDto;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
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

	private List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();

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

		try {
			documentCategoryList = documentCategoryRepository
					.findAllByIsActiveTrueAndIsDeletedFalse(DocumentCategory.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategory -> {
				DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
				try {
					dataMapper.map(documentCategory, documentCategoryDto, true, null, null, true);
				} catch (DataMapperException e) {
					throw new MasterDataServiceException(
							DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(),
							e.getMessage());
				}
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

		try {
			documentCategoryList = documentCategoryRepository
					.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryList.forEach(documentCategoryList -> {
				DocumentCategoryDto documentCategoryDto = new DocumentCategoryDto();
				try {
					dataMapper.map(documentCategoryList, documentCategoryDto, true, null, null, true);
				} catch (DataMapperException e) {
					throw new MasterDataServiceException(
							DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(),
							e.getMessage());
				}
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

		DocumentCategory documentCategory;
		DocumentCategoryDto documentCategoryDto;
		try {
			documentCategory = documentCategoryRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(code,
					langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(), e.getMessage());
		}

		if (documentCategory != null) {
			try {
				documentCategoryDto = dataMapper.map(documentCategory, DocumentCategoryDto.class, true, null, null,
						true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		List<DocumentCategoryDto> documentCategoryDtoList = new ArrayList<>();
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
	public PostResponseDto addDocumentCategoriesData(DocumentCategoryRequestDto category) {

		List<DocumentCategory> entities = metaUtils.setCreateMetaData(category.getRequest().getDocumentCategories(),
				DocumentCategory.class);
		List<DocumentCategory> documentCategories;
		try {
			documentCategories = documentCategoryRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getMessage());
		}
		List<CodeAndLanguageCodeId> codeLangCodeIds = new ArrayList<>();
		documentCategories.forEach(documentCategory -> {
			CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
			try {
				dataMapper.map(documentCategory, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setResults(codeLangCodeIds);
		return postResponseDto;
	}
}
