package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.masterdata.constant.BlacklistedWordsErrorCode;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsFetchException;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsIllegalArgException;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsMappingException;
import io.mosip.kernel.masterdata.exception.NoBlacklistedWordsFoundException;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 06-11-2018
 */

@Service
public class BlacklistedWordsServiceImpl implements BlacklistedWordsService {

	@Autowired
	private BlacklistedWordsRepository blacklistedWordsRepository;
	@Autowired
	private ObjectMapperUtil mapperUtil;

	@Override
	public BlacklistedWordsResponseDto getAllBlacklistedWordsBylangCode(String langCode) {
		List<BlacklistedWordsDto> wordsDto = null;
		List<BlacklistedWords> words = null;
		if (langCode != null && !langCode.isEmpty()) {
			try {
				words = blacklistedWordsRepository.findAllByLangCode(langCode);
			} catch (DataAccessException accessException) {
				throw new BlacklistedWordsFetchException(
						BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
						BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage());
			}
			if (words != null && !words.isEmpty()) {
				try {
					wordsDto = mapperUtil.mapAll(words, BlacklistedWordsDto.class);
				} catch (IllegalArgumentException | ConfigurationException | MappingException exception) {
					throw new BlacklistedWordsMappingException(
							BlacklistedWordsErrorCode.BLACKLISTED_WORDS_MAPPING_EXCEPTION.getErrorCode(),
							BlacklistedWordsErrorCode.BLACKLISTED_WORDS_MAPPING_EXCEPTION.getErrorMessage());
				}
			} else {
				throw new NoBlacklistedWordsFoundException(
						BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
						BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
			}
		} else {
			throw new BlacklistedWordsIllegalArgException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_LANG_CODE_ARG_MISSING.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_LANG_CODE_ARG_MISSING.getErrorMessage());
		}

		return new BlacklistedWordsResponseDto(wordsDto);
	}

}
