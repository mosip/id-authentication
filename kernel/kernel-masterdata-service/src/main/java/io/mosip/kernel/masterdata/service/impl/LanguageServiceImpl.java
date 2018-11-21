package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.LanguageErrorCode;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageRequestResponseDto;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.utils.CheckUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 *
 * @author Bal Vikash Sharma
 * @Version 1.0.0
 */
@Service
public class LanguageServiceImpl implements LanguageService {

	/**
	 * Repository used for CRUD operation.
	 */
	@Autowired
	private LanguageRepository languageRepository;

	/**
	 * Helper class to map objects.
	 */
	@Autowired
	private ObjectMapperUtil mapperUtil;

	/**
	 * Helper class to map dto to entity.
	 */
	@Autowired
	private MetaDataUtils metaDataUtils;

	/**
	 * This method fetch all Languages present in database.
	 * 
	 * @throws LanguageFetchException
	 *             when exception raise during fetch of Language details.
	 * @throws LanguageNotFoundException
	 *             when no records found for given parameter(langCode).
	 * @throws LanguageMappingException
	 *             when error occurs while mapping.
	 */
	@Override
	public LanguageRequestResponseDto getAllLaguages() {
		LanguageRequestResponseDto languageRequestResponseDto = new LanguageRequestResponseDto();
		List<LanguageDto> languageDtos = null;
		List<Language> languages = null;

		try {
			languages = languageRepository.findAllByIsActiveTrueAndIsDeletedFalse();
		} catch (DataAccessException dataAccessException) {
			throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (languages != null && !languages.isEmpty()) {
			languageDtos = mapperUtil.mapAll(languages, LanguageDto.class);
		} else {
			throw new DataNotFoundException(LanguageErrorCode.NO_LANGUAGE_FOUND_EXCEPTION.getErrorCode(),
					LanguageErrorCode.NO_LANGUAGE_FOUND_EXCEPTION.getErrorMessage());
		}

		languageRequestResponseDto.setLanguages(languageDtos);
		return languageRequestResponseDto;
	}

	@Override
	public LanguageRequestResponseDto saveAllLanguages(LanguageRequestResponseDto dto) {
		if (CheckUtils.isNullEmpty(dto) || CheckUtils.isNullEmpty(dto.getLanguages())) {
			throw new RequestException(LanguageErrorCode.LANGUAGE_REQUEST_PARAM_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_REQUEST_PARAM_EXCEPTION.getErrorMessage());
		}

		LanguageRequestResponseDto languageRequestResponseDto = new LanguageRequestResponseDto();
		List<LanguageDto> languageDtos = dto.getLanguages();
		try {
			List<Language> languages = metaDataUtils.setCreateMetaData(languageDtos, Language.class);
			List<Language> createdLanguages = languageRepository.saveAll(languages);
			if (CheckUtils.isNullEmpty(createdLanguages)) {
				throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorCode(),
						LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorMessage());
			}
			List<LanguageDto> createdLanguageDtos = mapperUtil.mapAll(createdLanguages, LanguageDto.class);
			languageRequestResponseDto.setLanguages(createdLanguageDtos);

		} catch (Exception e) {
			throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorMessage());
		}
		return languageRequestResponseDto;
	}

}
