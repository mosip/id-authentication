package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.LanguageErrorCode;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.LanguageResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

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
	private MapperUtils mapperUtil;

	/**
	 * Helper class to map dto to entity.
	 */
	@Autowired
	private MetaDataUtils metaDataUtils;

	/**
	 * (non-Javadoc)
	 * 
	 * @see LanguageService#getAllLaguages()
	 */
	@Override
	public LanguageResponseDto getAllLaguages() {
		LanguageResponseDto languageResponseDto = new LanguageResponseDto();
		List<LanguageDto> languageDtos = null;
		List<Language> languages = null;

		try {
			languages = languageRepository.findAllByIsDeletedFalseOrIsDeletedIsNull();
		} catch (DataAccessException dataAccessException) {
			throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_FETCH_EXCEPTION.getErrorMessage(), dataAccessException);
		}

		if (languages != null && !languages.isEmpty()) {
			languageDtos = mapperUtil.mapAll(languages, LanguageDto.class);
		} else {
			throw new DataNotFoundException(LanguageErrorCode.NO_LANGUAGE_FOUND_EXCEPTION.getErrorCode(),
					LanguageErrorCode.NO_LANGUAGE_FOUND_EXCEPTION.getErrorMessage());
		}

		languageResponseDto.setLanguages(languageDtos);
		return languageResponseDto;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see LanguageService#saveLanguage(RequestDto)
	 */
	public CodeResponseDto saveLanguage(RequestDto<LanguageDto> requestDto) {

		try {
			Language language = metaDataUtils.setCreateMetaData(requestDto.getRequest(), Language.class);
			Language savedLanguage = languageRepository.create(language);
			return mapperUtil.map(savedLanguage, CodeResponseDto.class);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorMessage() + ": "
							+ ExceptionUtils.parseException(e));
		}
	}

}
