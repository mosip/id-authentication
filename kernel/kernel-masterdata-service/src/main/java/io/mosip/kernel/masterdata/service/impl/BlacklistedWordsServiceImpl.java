package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.ApplicationErrorCode;
import io.mosip.kernel.masterdata.constant.BlacklistedWordsErrorCode;
import io.mosip.kernel.masterdata.constant.UpdateQueryConstants;
import io.mosip.kernel.masterdata.dto.BlackListedWordsUpdateDto;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.dto.getresponse.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.BlacklistedWordsExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.entity.id.WordAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterdataSearchHelper;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.PageUtils;

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

	@PersistenceContext
	private EntityManager entityManager;

	private CriteriaBuilder criteriaBuilder;

	@Autowired
	MasterdataSearchHelper masterDataSearchHelper;
	/**
	 * Autowired reference for {@link DataMapper}
	 */

	@Qualifier("blacklistedWordsToWordAndLanguageCodeIDDefaultMapper")
	@Autowired
	private DataMapper<BlacklistedWords, WordAndLanguageCodeID> blacklistedWordsToWordAndLanguageCodeIDDefaultMapper;

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
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(accessException));
		}
		if (words != null && !words.isEmpty()) {
			wordsDto = MapperUtils.mapAll(words, BlacklistedWordsDto.class);
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
		} catch (DataAccessException | DataAccessLayerException accessException) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(accessException));
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
	public WordAndLanguageCodeID createBlackListedWord(BlacklistedWordsDto blackListedWordsRequestDto) {
		BlacklistedWords entity = MetaDataUtils.setCreateMetaData(blackListedWordsRequestDto, BlacklistedWords.class);
		BlacklistedWords blacklistedWords;
		entity.setWord(entity.getWord().toLowerCase());
		try {
			blacklistedWords = blacklistedWordsRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorCode(),
					ApplicationErrorCode.APPLICATION_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}

		return blacklistedWordsToWordAndLanguageCodeIDDefaultMapper.map(blacklistedWords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BlacklistedWordsService#
	 * updateBlackListedWord(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public WordAndLanguageCodeID updateBlackListedWord(BlackListedWordsUpdateDto wordDto) {
		WordAndLanguageCodeID wordAndLanguageCodeID = null;
		int noOfRowAffected = 0;
		Map<String, Object> params = bindDtoToMap(wordDto);
		try {
			if (wordDto.getDescription() != null) {
				noOfRowAffected = blacklistedWordsRepository.createQueryUpdateOrDelete(
						UpdateQueryConstants.BLACKLISTED_WORD_UPDATE_QUERY_WITH_DESCRIPTION.getQuery(), params);

			} else {
				noOfRowAffected = blacklistedWordsRepository.createQueryUpdateOrDelete(
						UpdateQueryConstants.BLACKLISTED_WORD_UPDATE_QUERY_WITHOUT_DESCRIPTION.getQuery(), params);
			}
			if (noOfRowAffected != 0)
				wordAndLanguageCodeID = mapToWordAndLanguageCodeID(wordDto);
			else {
				throw new RequestException(BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
						BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorMessage());
		}
		return wordAndLanguageCodeID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BlacklistedWordsService#
	 * deleteBlackListedWord(java.lang.String)
	 */
	@Override
	public String deleteBlackListedWord(String blackListedWord) {
		int noOfRowAffected = 0;
		try {
			noOfRowAffected = blacklistedWordsRepository.deleteBlackListedWord(blackListedWord,
					LocalDateTime.now(ZoneId.of("UTC")));

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorMessage());
		}
		if (noOfRowAffected == 0) {
			throw new RequestException(BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
					BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
		}

		return blackListedWord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.BlacklistedWordsService#
	 * getBlackListedWords()
	 */
	@Override
	public PageDto<BlacklistedWordsExtnDto> getBlackListedWords(int pageNumber, int pageSize, String sortBy,
			String orderBy) {
		List<BlacklistedWordsExtnDto> wordsDto = null;
		PageDto<BlacklistedWordsExtnDto> pageDto = null;
		try {
			Page<BlacklistedWords> pageData = blacklistedWordsRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				wordsDto = MapperUtils.mapAll(pageData.getContent(), BlacklistedWordsExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						wordsDto);
			} else {
				throw new DataNotFoundException(BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
						BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
			}
		} catch (DataAccessException accessException) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_FETCH_EXCEPTION.getErrorMessage()
							+ ExceptionUtils.parseException(accessException));
		}
		return pageDto;
	}

	private WordAndLanguageCodeID mapToWordAndLanguageCodeID(BlackListedWordsUpdateDto dto) {
		WordAndLanguageCodeID wordAndLanguageCodeID;
		wordAndLanguageCodeID = new WordAndLanguageCodeID();
		if (dto.getWord() != null)
			wordAndLanguageCodeID.setWord(dto.getWord());
		if (dto.getLangCode() != null)
			wordAndLanguageCodeID.setLangCode(dto.getLangCode());
		return wordAndLanguageCodeID;
	}

	private Map<String, Object> bindDtoToMap(BlackListedWordsUpdateDto dto) {
		Map<String, Object> params = new HashMap<>();
		if (dto.getWord() != null && !dto.getWord().isEmpty())
			params.put("word", dto.getWord());
		if (dto.getOldWord() != null && !dto.getOldWord().isEmpty())
			params.put("oldWord", dto.getOldWord());
		if (dto.getDescription() != null && !dto.getDescription().isEmpty())
			params.put("description", dto.getDescription());
		params.put("isActive", dto.getIsActive());
		params.put("updatedBy", MetaDataUtils.getContextUser());
		params.put("updatedDateTime", LocalDateTime.now(ZoneId.of("UTC")));
		params.put("langCode", dto.getLangCode());
		return params;
	}

	@Override
	public WordAndLanguageCodeID updateBlackListedWordExceptWord(BlacklistedWordsDto blacklistedWordsDto) {
		WordAndLanguageCodeID id = null;
		BlacklistedWords wordEntity = null;
		blacklistedWordsDto.setWord(blacklistedWordsDto.getWord().toLowerCase());
		try {
			wordEntity = blacklistedWordsRepository.findByWordAndLangCode(blacklistedWordsDto.getWord(),
					blacklistedWordsDto.getLangCode());
			if (wordEntity != null) {
				MetaDataUtils.setUpdateMetaData(blacklistedWordsDto, wordEntity, false);
				wordEntity = blacklistedWordsRepository.update(wordEntity);
				id = MapperUtils.map(wordEntity, WordAndLanguageCodeID.class);
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorCode(),
					BlacklistedWordsErrorCode.BLACKLISTED_WORDS_UPDATE_EXCEPTION.getErrorMessage());
		}

		if (id == null) {
			throw new RequestException(BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorCode(),
					BlacklistedWordsErrorCode.NO_BLACKLISTED_WORDS_FOUND.getErrorMessage());
		}

		return id;
	}

	@Override
	public PageResponseDto<BlacklistedWordsExtnDto> searchBlackListedWords(SearchDto dto) {
		PageResponseDto<BlacklistedWordsExtnDto> pageDto = new PageResponseDto<>();
		List<BlacklistedWordsExtnDto> blackListedWords = null;
		Page<BlacklistedWords> page = masterDataSearchHelper.searchMasterdata(BlacklistedWords.class, dto, null);
		if (page.getContent() != null && !page.getContent().isEmpty()) {
			pageDto = PageUtils.pageResponse(page);
			blackListedWords = MapperUtils.mapAll(page.getContent(), BlacklistedWordsExtnDto.class);
			pageDto.setData(blackListedWords);
		}
		return pageDto;
	}

	@Override
	public FilterResponseDto blackListedWordsFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		for (FilterDto filterDto : filterValueDto.getFilters()) {
			masterDataSearchHelper.filterValues(BlacklistedWords.class, filterDto.getColumnName(), filterDto.getType(),
					filterValueDto.getLanguageCode()).forEach(filterValue -> {
						ColumnValue columnValue = new ColumnValue();
						columnValue.setFieldID(filterDto.getColumnName());
						columnValue.setFieldValue(filterValue);
						columnValueList.add(columnValue);
					});
		}
		filterResponseDto.setFilters(columnValueList);
		return filterResponseDto;
	}
}
