package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.BlacklistedWordsErrorCode;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Service implementation class for {@link BlacklistedWordsService}.
 * 
 * @author Abhishek Kumar
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
@Service
public class BlacklistedWordsServiceImpl implements BlacklistedWordsService {

	/**
	 * Autowired reference for {@link BlacklistedWordsRepository}.
	 */
	@Autowired
	private BlacklistedWordsRepository blacklistedWordsRepository;
	/**
	 * Autowired reference for {@link DataMapper}
	 */
	@Autowired
	private DataMapper dataMapper;
	/**
	 * Autowired reference for {@link MapperUtils}
	 */
	@Autowired
	private MapperUtils mapperUtil;
	/**
	 * Autowired reference for {@link MetaDataUtils}
	 */
	@Autowired
	private MetaDataUtils metaUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BlacklistedWordsService#
	 * getAllBlacklistedWordsBylangCode(java.lang.String)
	 */
	@Override
	public BlacklistedWordsResponseDto getAllBlacklistedWordsBylangCode(String langCode) {
		List<BlacklistedWordsDto> wordsDto = null;
		List<BlacklistedWords> words = null;
		try {
			words = blacklistedWordsRepository.findAllByLangCode(langCode);
		} catch (DataAccessException accessException) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage());
		}
		if (words != null && !words.isEmpty()) {
			wordsDto = mapperUtil.mapAll(words, BlacklistedWordsDto.class);
		} else {
			throw new DataNotFoundException(BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
					BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
		}

		return new BlacklistedWordsResponseDto(wordsDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.BlacklistedWordsService#validateWord(java.
	 * util.List)
	 */
	@Override
	public boolean validateWord(List<String> words) {
		List<String> wordList = new ArrayList<>();
		boolean isValid = true;
		List<BlacklistedWords> blackListedWordsList = null;
		try {
			blackListedWordsList = blacklistedWordsRepository.findAllByIsDeletedFalseOrIsDeletedNull();
		} catch (DataAccessException accessException) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage());
		}
		for (BlacklistedWords blackListedWords : blackListedWordsList) {
			wordList.add(blackListedWords.getWord());
		}
		words.replaceAll(String::toLowerCase);
		wordList.replaceAll(String::toLowerCase);

		for (String temp : wordList) {
			if (words.contains(temp)) {
				isValid = false;
			}
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.BlacklistedWordsService#addBlackListedWord
	 * (io.mosip.kernel.masterdata.dto.BlackListedWordsRequestDto)
	 */
	@Override
	public WordAndLanguageCodeID createBlackListedWord(RequestDto<BlacklistedWordsDto> blackListedWordsRequestDto) {
		BlacklistedWords entity = metaUtils.setCreateMetaData(blackListedWordsRequestDto.getRequest(),
				BlacklistedWords.class);
		BlacklistedWords blacklistedWords;
		try {
			blacklistedWords = blacklistedWordsRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		WordAndLanguageCodeID wordAndLanguageCodeID = new WordAndLanguageCodeID();
		dataMapper.map(blacklistedWords, wordAndLanguageCodeID, true, null, null, true);
		return wordAndLanguageCodeID;
	}
}
