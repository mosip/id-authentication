package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.LanguageErrorCode;
import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageResponseDto;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.service.LanguageService;
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
			languages = languageRepository.findAllByIsDeletedFalse();
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
	 * @see LanguageService#saveLanguage(LanguageDto)
	 */
	@Override
	public String saveLanguage(LanguageDto dto) {

		try {
			Language languages = metaDataUtils.mapDtoToEntity(dto, Language.class);
			return languageRepository.create(languages).getCode();
		} catch (HibernateException e) {
			throw new MasterDataServiceException(LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorCode(),
					LanguageErrorCode.LANGUAGE_CREATE_EXCEPTION.getErrorMessage(), e);
		}
	}

}
