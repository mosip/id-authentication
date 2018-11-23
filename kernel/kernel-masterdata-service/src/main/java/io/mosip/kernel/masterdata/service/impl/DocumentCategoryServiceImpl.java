package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.DocumentCategoryDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.DocumentCategoryRepository;
import io.mosip.kernel.masterdata.service.DocumentCategoryService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@Service
public class DocumentCategoryServiceImpl implements DocumentCategoryService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ObjectMapperUtil objectMapperUtil;

	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	private List<DocumentCategory> documentCategoryList;

	private List<DocumentCategoryDto> documentCategoryDtoList;

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
	public List<DocumentCategoryDto> getAllDocumentCategory() {
		try {
			documentCategoryList = documentCategoryRepository.findAllByIsActiveTrueAndIsDeletedFalse(DocumentCategory.class);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryDtoList = objectMapperUtil.mapAll(documentCategoryList, DocumentCategoryDto.class);
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return documentCategoryDtoList;
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
	public List<DocumentCategoryDto> getAllDocumentCategoryByLaguageCode(String langCode) {
		try {
			documentCategoryList = documentCategoryRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (!(documentCategoryList.isEmpty())) {
			documentCategoryDtoList = objectMapperUtil.mapAll(documentCategoryList, DocumentCategoryDto.class);
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return documentCategoryDtoList;
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
	public DocumentCategoryDto getDocumentCategoryByCodeAndLangCode(String code, String langCode) {
		DocumentCategory documentCategory;
		DocumentCategoryDto documentCategoryDto;
		try {
			documentCategory = documentCategoryRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(code, langCode);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_FETCH_EXCEPTION.getErrorMessage());
		}

		if (documentCategory != null) {
			documentCategoryDto = modelMapper.map(documentCategory, DocumentCategoryDto.class);
		} else {
			throw new DataNotFoundException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorCode(),
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return documentCategoryDto;
	}

}
