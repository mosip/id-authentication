package io.mosip.kernel.masterdata.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.TitleErrorCode;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.getresponse.TitleResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.service.TitleService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * Implementing service class for fetching titles from master db
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class TitleServiceImpl implements TitleService {

	@Autowired
	private TitleRepository titleRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TitleService#getAllTitles()
	 */
	@Override
	public TitleResponseDto getAllTitles() {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> titles = null;
		try {
			titles = titleRepository.findAll(Title.class);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage()+
					ExceptionUtils.parseException(e));
		}
		if (titles != null && !titles.isEmpty()) {
			titleDto = MapperUtils.mapAll(titles, TitleDto.class);
		} else {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);
		return titleResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#getByLanguageCode(java.lang.
	 * String)
	 */
	@Override
	public TitleResponseDto getByLanguageCode(String languageCode) {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> title = null;

		try {
			title = titleRepository.getThroughLanguageCode(languageCode);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage()+ExceptionUtils.parseException(e));
		}
		if (title.isEmpty()) {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleDto = MapperUtils.mapAll(title, TitleDto.class);

		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);

		return titleResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#saveTitle(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID saveTitle(RequestDto<TitleDto> titleRequestDto) {
		Title entity = MetaDataUtils.setCreateMetaData(titleRequestDto.getRequest(), Title.class);
		Title title;
		try {
			title = titleRepository.create(entity);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_INSERT_EXCEPTION.getErrorCode(),
					ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(title, codeLangCodeId);
		return codeLangCodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#updateTitle(io.mosip.kernel.
	 * masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID updateTitle(RequestDto<TitleDto> titles) {

		TitleDto titleDto = titles.getRequest();

		CodeAndLanguageCodeID titleId = new CodeAndLanguageCodeID();

		MapperUtils.mapFieldValues(titleDto, titleId);
		try {

			Title title = titleRepository.findById(Title.class, titleId);

			if (title != null) {
				MetaDataUtils.setUpdateMetaData(titleDto, title, false);
				titleRepository.update(title);
			} else {
				throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
						TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_UPDATE_EXCEPTION.getErrorMessage()+ExceptionUtils.parseException(e));
		}
		return titleId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#deleteTitle(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Transactional
	public CodeResponseDto deleteTitle(String code) {
		try {

			final List<Title> titleList = titleRepository.findByCode(code);

			if (!titleList.isEmpty()) {
				titleList.stream().map(MetaDataUtils::setDeleteMetaData).forEach(titleRepository::update);

			} else {
				throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
						TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_DELETE_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_DELETE_EXCEPTION.getErrorMessage()+ExceptionUtils.parseException(e));
		}
		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(code);
		return responseDto;
	}

}
